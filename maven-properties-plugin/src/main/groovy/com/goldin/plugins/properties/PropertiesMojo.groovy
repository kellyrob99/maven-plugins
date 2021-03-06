package com.goldin.plugins.properties

import com.goldin.gcommons.GCommons
import com.goldin.gcommons.util.GroovyConfig
import com.goldin.plugins.common.BaseGroovyMojo
import com.goldin.plugins.common.GMojoUtils
import org.apache.maven.plugin.MojoExecutionException
import org.apache.maven.plugin.MojoFailureException
import org.jfrog.maven.annomojo.annotations.MojoGoal
import org.jfrog.maven.annomojo.annotations.MojoParameter
import org.jfrog.maven.annomojo.annotations.MojoPhase

@MojoGoal( 'set-properties' )
@MojoPhase( 'validate' )
class PropertiesMojo extends BaseGroovyMojo
{
    @MojoParameter ( required = false )
    public Property[] properties

    @MojoParameter ( required = false )
    public Property   property

    @MojoParameter ( required = false )
    public boolean verbose = true

    @MojoParameter ( required = false )
    public GroovyConfig groovyConfig = new GroovyConfig()

    private Property[] properties() { GCommons.general().array( this.properties, this.property, Property ) }


    @Override
    public void doExecute() throws MojoExecutionException, MojoFailureException
    {
        for ( property in properties())
        {
            String name      = property.name?.trim()
            String value     = property.value?.trim()
            def    isVerbose = GCommons.general().choose( property.verbose, verbose )

            if ( value.startsWith( '{{' ) && value.endsWith( '}}' ))
            {
                groovyConfig.verbose = isVerbose
                value = GMojoUtils.groovy( value, String, groovyConfig )
            }

            GMojoUtils.setProperty( name, value, '', isVerbose )
        }
    }
}
