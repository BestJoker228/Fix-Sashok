/*launcher, ������ �� 30.10.2012, ������: 121 */

package net.launcher.run;

public class Settings
{
	/** ��������� ��������� �������� */
	public static final String 		title		 	 = "Launcher"; //��������� ��������
	public static final String 		titleInGame  	 = "Minecraft"; //��������� �������� ����� �����������
	public static final String 		basedir			 = "AppData"; //������������ ����� ��� Minecraft (������ ��� Windows) [ProgramFiles, AppData]
	public static final String 		baseconf		 = ".voxelaria"; //����� � ������ ������������
	public static final String		pathconst		 = ".voxelaria/%SERVERNAME%"; //����������� ���� � ����� � MC
	public static final String        skins            = "MinecraftSkins/"; //����� ������
	public static final String        cloaks           = "MinecraftCloaks/"; //����� ������
	/** ��������� ����������� */
	public static final String 	domain	 	 	 = "alexandrage.dyndns.org";//����� �����
	public static final String  siteDir		  	 = "site";//����� � ������� �������� �� �����
	public static final String  updateFile		 = "http://alexandrage.dyndns.org/site/Launcher.jar";//������ �� ���� ���������� ��������
	public static final String 	buyVauncherLink  = "http://plati.ru/"; //������ �� �������� ������� ��������

	/** ��� ��������� ���� */
	public static final String  defaultUsername  = "player"; //��� ������������ ��� ��������� ����
	public static final String  defaultSession   = "123456"; //����� ������ ��� ��������� ����

	/** ��������� �������� */
	public static final String[] servers =
	{
		"voxelaria, test, 25565, 1.5.x",
	};

	/** ��������� ������ ������ **/
	public static final String[] links = 
	{
		//��� ���������� �������� � ����� ������ #
		" ����������� ::http://",
	};

	/** ��������� ��������� �������� */
	public static boolean useAutoenter			 =  false; //������������ ������� ���������� �� ��������� ������
	
	
	public static boolean useMulticlient		 =  true; //������������ ������� "�� ������� �� ������"
	public static boolean useStandartWB			 =  true; //������������ ����������� ������� ��� �������� ������
	public static boolean usePersonal		 	 =  true; //������������ ������ �������
	public static boolean customframe 			 =  true; //������������ ��������� �����
	public static boolean useOffline 			 =  true; //������������ ����� �������
	public static boolean useConsoleHider		 =  true; //������������ ������� ������� �������
	public static boolean useModCheckerTimer	 =  true; //������ 30 ������ ���� ����� ���������������

	public static final String protectionKey			 = "tH@nKy0u.d_@rT"; //���� ������ ������. ������ ��� �� ��������.

	public static final boolean debug		 	 =  true;  //���������� ��� �������� �������� (�������)(true/false)
	public static final boolean drawTracers		 =  false; //������������ ������� ��������� ��������
	public static final String masterVersion  	 = "final_RC4"; //������ ��������

	public static final boolean patchDir 		 =  true; //������������ �������������� ������ ���������� ���� (true/false)
	public static final String mcclass			 = "net.minecraft.client.Minecraft";
	public static final String[] mcversions		 =
	{
		"1.7.3::af", "1.8.1::ag", "1.2.5::aj", "1.3.x::am", "1.4.x::an", "1.5.x::an"
	};
	
	public static void onStart() { /*  */ }
	public static void onStartMinecraft() {}
}