package mod.flatcoloredblocks.gui;

/*x
public class ConfigGui extends GuiConfig
{

    public ConfigGui(
            final GuiScreen parent )
    {
        super( parent, getConfigElements(), FlatColoredBlocks.MODID, false, false, GuiConfig.getAbridgedConfigPath( FlatColoredBlocks.instance.config.getFilePath().getAbsolutePath() ) );
    }

    @Override
    public void initGui()
    {
        for ( final IConfigElement e : configElements )
        {
            if ( e instanceof ConfigElement )
            {
                final ConfigElement cat = (ConfigElement) e;
                if ( cat.getName().equals( "startupgui" ) )
                {
                    configElements.remove( e );
                    break;
                }
            }
        }

        super.initGui();
    }

    private static List<IConfigElement> getConfigElements()
    {
        final List<IConfigElement> list = new ArrayList<IConfigElement>();

        final ModConfig config = FlatColoredBlocks.instance.config;

        for ( final String cat : config.getCategoryNames() )
        {
            final ConfigCategory cc = config.getCategory( cat );

            if ( cat == "startupgui" || cc.isChild() )
            {
                continue;
            }

            list.add( new ConfigElement( cc ) );
        }

        return list;
    }

}
*/
