package com.goldin.plugins.hudson

import com.goldin.gcommons.GCommons

/**
 * Source repository location
 */
class Repository
{
    String  remote
    String  remoteLink
    String  local

    /**
     * CVS-specific properties
     */

    String  cvsModule               = ''
    String  cvsBranch               = ''
    String  cvsRsh                  = ''     // "CVS_RSH" advanced option
    String  cvsExcludedRegions      = ''     // "Excluded Regions" advanced option
    String  cvsRepositoryBrowser    = ''     // "Repository browser" advanced option, 'ViewCVS' or 'FishEye'
    String  cvsRepositoryBrowserUrl = ''     // "Repository browser URL" advanced option
    boolean cvsTag                  = false  // "This is a tag, not a branch" option
    boolean cvsUpdate               = true   // "Use update" advanced option
    boolean cvsLegacy               = false  // "Legacy mode" advanced option

    String getCvsRepositoryBrowserClass()
    {
        [ ViewCVS : 'hudson.scm.browsers.ViewCVS',
          FishEye : 'hudson.scm.browsers.FishEyeCVS' ][ cvsRepositoryBrowser ]
    }

    /**
     * Git-specific properties
     */

    String  gitName    = 'origin'
    String  gitBranch  = 'master'
    String  gitRefspec = '+refs/heads/*:refs/remotes/origin/*'
    boolean gitorious  = false


    /**
    * Whether this repository is a Git/Gitorious/SVN/CVS repository
    */
    boolean isGit()        { remote.endsWith( '.git' ) }
    boolean isGitorious () { assert isGit(); ( remote.contains( 'gitorious.org/' ) || this.gitorious ) }
    boolean isSvn()        { ( remote.startsWith( 'http://' )   || remote.startsWith( 'https://' ) || remote.startsWith( 'svn://' )) }
    boolean isCVS()        { ( remote.startsWith( ':pserver:' ) || ( ! ( isGit() || isSvn()))) }


   /**
    * Sets repository remote and HTTP URL.
    * @param remote repository remote URL
    */
    void setRemote( String remote )
    {
        this.remote     = remote.replaceAll( '/$', '' ) // Trimming trailing '/'
        this.remoteLink = this.remote

        if ( isGit())
        {
            this.remoteLink =

            /**
             * http://repo.or.cz/
             */

            // "git://repo.or.cz/jetpack.git" => "http://repo.or.cz/w/jetpack.git"
            remote.startsWith( 'git://repo.or.cz/' )    ? remote.replace( 'git://repo.or.cz/',
                                                                          'http://repo.or.cz/w/' ) :

            // "http://repo.or.cz/r/jetpack.git" => "http://repo.or.cz/w/jetpack.git"
            remote.startsWith( 'http://repo.or.cz/r/' ) ? remote.replace( 'http://repo.or.cz/r/',
                                                                          'http://repo.or.cz/w/' ) :
            /**
             * http://github.com/
             * http://gitorious.org/
             */

            // "git://github.com/evgeny-goldin/WideFinder.git" => "http://github.com/evgeny-goldin/WideFinder"
            // "git://gitorious.org/qt/qt.git"                 => "http://gitorious.org/qt/qt"
            ( remote.startsWith( 'git://' ) ? remote.replace( 'git://', 'http://' ) :

            // "git@github.com:evgeny-goldin/WideFinder.git" => "http://github.com/evgeny-goldin/WideFinder"
              remote.startsWith( 'git@' )   ? remote.replace( ':', '/' ).replace( 'git@', 'http://' ) :

            // "https://evgeny-goldin@github.com/evgeny-goldin/WideFinder.git" => "http://github.com/evgeny-goldin/WideFinder"
            // "http://git.gitorious.org/qt/qt.git"                            => "http://git.gitorious.org/qt/qt"
              remote.endsWith( '.git' )     ? remote.replaceFirst( 'https?://.+@', 'http://' ) :
                                              remote ).
            replaceFirst( /\.git$/, '' )
        }
        else if ( isCVS())
        {
            /**
             * http://durak.org/cvswebsites/howto-cvs/node9.html
             * http://www.idevelopment.info/data/Programming/change_management/unix_cvs/PROGRAMMING_Logging_into_CVS.shtml
             * ":pserver:<username>@<computername>:<repository>"
             * ":pserver:username@interactivate.com:/usr/local/cvs-repository"
             * ":pserver:anonymous@simple-wicket.cvs.sourceforge.net:/cvsroot/simple-wicket"
             */

            def matcher     = ( remote =~ /^:pserver:[^@]+@([^:]+):.+$/ )
            this.remoteLink = ( matcher ? "http://${ matcher[ 0 ][ 1 ] }" + ( this.cvsModule ? "/viewvc/${ this.cvsModule }/${ this.cvsModule }" : '' ) :
                                          remote )
        }

        assert ( ! this.remote.endsWith( '/' )) && ( ! this.remoteLink.endsWith( '/' ))
    }


    /**
     * Retrieves a remote HTTP link for Git branch
     *
     * @return remote HTTP link for Git branch
     */
     String getGitRemoteBranchLink()
     {
         assert isGit()
         "${ remoteLink }/${ isGitorious() ? 'trees' : 'tree' }/${ gitBranch }"
     }


   /**
    * Retrieves a remote HTTP link for the path specified.
    *
    * @param path project path
    * @return     remote HTTP link for the path specified
    */
    String getRemotePathLink( String path )
    {
        GCommons.verify().notNullOrEmpty( path )

        ( isGit() ? "${ remoteLink }/${ isGitorious() ? 'blobs' : 'blob' }/${ gitBranch }" :
                    remoteLink ) +
        "/$path"
    }
}
