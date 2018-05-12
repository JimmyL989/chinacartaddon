package com.hybris.chinacartaddon.jalo;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import org.apache.log4j.Logger;

import com.hybris.chinacartaddon.constants.ChinacartaddonConstants;
import com.sap.df.chinacartaddon.jalo.GeneratedChinacartaddonManager;

@SuppressWarnings("PMD")
public class ChinacartaddonManager extends GeneratedChinacartaddonManager
{
	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger( ChinacartaddonManager.class.getName() );
	
	public static final ChinacartaddonManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (ChinacartaddonManager) em.getExtension(ChinacartaddonConstants.EXTENSIONNAME);
	}
	
}
