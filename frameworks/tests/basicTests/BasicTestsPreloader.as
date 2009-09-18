package 
{
import flash.system.ApplicationDomain;
import mx.preloaders.DownloadProgressBar;

public class BasicTestsPreloader extends DownloadProgressBar
{
	public function BasicTestsPreloader()
	{
		super();
		if (ApplicationDomain.currentDomain.hasDefinition("mx.core::UIComponent"))
		{
			throw new Error("UIComponent found in frame 1");
		}
	}
}

}

