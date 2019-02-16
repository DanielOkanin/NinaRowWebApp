package engine;

import engine.gameProperties.GameDescriptor;

public class XmlGameFileAnalyzed {
    private GameDescriptor m_GameDescriptor;
    private boolean m_IsProper;
    private String m_Error;
    private String m_GameTitle;

    public XmlGameFileAnalyzed(GameDescriptor i_GameDescriptor, boolean i_isProper, String i_Error, String i_GameTitle) {
        m_GameDescriptor = i_GameDescriptor;
        m_IsProper = i_isProper;
        m_Error = i_Error;
        m_GameTitle = i_GameTitle;
    }

    public GameDescriptor getGameDescriptor() {return  m_GameDescriptor;}
    public boolean getIsProper() {return m_IsProper;}
    public String getError() {return  m_Error;}
    public String getGameTitle() {return m_GameTitle;}
}
