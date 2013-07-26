package net.launcher.utils;

import static net.launcher.utils.BaseUtils.buildUrl;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import net.launcher.components.Frame;
import net.launcher.components.Game;
import net.launcher.components.PersonalContainer;
import net.launcher.run.Settings;

public class ThreadUtils
{
	public static String b =  "bin";
	public static String l =  "libraries.jar";
	public static String f =  "Forge.jar";
	public static String e =  "extra.jar";
	public static String m =  "minecraft.jar";
	public static UpdaterThread updaterThread;
	public static Thread serverPollThread;
	
	public static void updateNewsPage(final String url)
	{
		BaseUtils.send("Updating news page...");
		if(!BaseUtils.getPropertyBoolean("loadnews", true))
		{
			Frame.main.browser.setText("<center><font color=\"#F0F0F0\" style=\"font-family:Tahoma\">�������� �������� �� ��������</font></center>");
			return;
		}
		Frame.main.browser.setText("<center><font color=\"#F0F0F0\" style=\"font-family:Tahoma\">���������� ��������...</font></center>");
		Thread t = new Thread() { public void run()
		{
			try
			{
				Frame.main.browser.setPage(url);
				BaseUtils.send("Updating news page sucessful!");
			} catch (Exception e)
			{
				Frame.main.browser.setText("<center><font color=\"#FF0000\" style=\"font-family:Tahoma\"><b>������ �������� ��������:<br>" + e.toString() + "</b></font></center>");
				BaseUtils.send("Updating news page fail! (" + e.toString() + ")");
			}
			interrupt();
		}};
		t.setName("Update news thread");
		t.start();
	}

	public static void auth(final boolean personal)
	{
		if(!personal && Frame.main.offline.isSelected())
		{
			new Game(null);
			return;
		}
		BaseUtils.send("Logging in, login: " + Frame.main.login.getText());
		Thread t = new Thread() {
		public void run()
		{ try {
			String answer = BaseUtils.execute(BaseUtils.buildUrl("launcher.php"), new Object[]
			{
				"action", "auth",
				"client", BaseUtils.getClientName(),
				"login", Frame.main.login.getText(),
				"password", new String(Frame.main.password.getPassword()),
			});
			boolean error = false;
			if(answer == null)
			{
				Frame.main.panel.tmpString = "������ �����������";
				error = true;
			} else if(answer.split("<br>").length != 3)
			{
				Frame.main.panel.tmpString = answer;
				error = true;
			} if(error)
			{
				Frame.main.panel.tmpColor = Color.red;
				try
				{
					sleep(2000);
				} catch (InterruptedException e) {}
				Frame.main.setAuthComp();
			} else
			{
				String version = answer.split("<br>")[0].split("<:>")[5];
				
				if(!version.equals(Settings.masterVersion))
				{
					Frame.main.setUpdateComp(version);
					return;
				}
				BaseUtils.send("Logging in successful");
				
				if(personal)
				{
					Frame.main.panel.tmpString = "�������� ������...";
					String personal = BaseUtils.execute(BaseUtils.buildUrl("launcher.php"), new Object[]
					{
						"action", "getpersonal",
						"login", Frame.main.login.getText(),
						"password", new String(Frame.main.password.getPassword())
					});
					
					if(personal == null)
					{
						Frame.main.panel.tmpString = "������ �����������";
						error = true;
					} else if(personal.split("<:>").length != 13 || personal.split("<:>")[0].length() != 7)
					{
						Frame.main.panel.tmpString = personal;
						error = true;
					} if(error)
					{
						Frame.main.panel.tmpColor = Color.red;
						try
						{
							sleep(2000);
						} catch (InterruptedException e) {}
						Frame.main.setAuthComp();
						return;
					} else
					{
						try {
						Frame.main.panel.tmpString = "�������� �����...";
						BufferedImage skinImage   = BaseUtils.getSkinImage(answer.split("<br>")[1].split("<:>")[0]);
						Frame.main.panel.tmpString = "�������� �����...";
						BufferedImage cloakImage  = BaseUtils.getCloakImage(answer.split("<br>")[1].split("<:>")[0]);
						Frame.main.panel.tmpString = "������� �����...";
						skinImage = ImageUtils.parseSkin(skinImage);
						Frame.main.panel.tmpString = "������� �����...";
						cloakImage= ImageUtils.parseCloak(cloakImage);
						Frame.main.panel.tmpString = BaseUtils.empty;
						PersonalContainer pc = new PersonalContainer(personal.split("<:>"), skinImage, cloakImage);
						Frame.main.setPersonal(pc);
						
						return;
						} catch(Exception e){ BaseUtils.throwException(e, Frame.main); return; }
					}
				}
				runUpdater(answer);
			} interrupt(); } catch(Exception e){ e.printStackTrace(); }
		}};
		t.setName("Auth thread");
		t.start();
	}
	
	public static void runUpdater(String answer)
	{
		boolean zipupdate = false;
		List<String> files = GuardUtils.updateMods_first(answer);
		String binfolder = BaseUtils.getMcDir() + File.separator + b + File.separator;
		
		if(!EncodingUtils.xorencode(EncodingUtils.inttostr(answer.split("<br>")[0].split("<:>")[0]), Settings.protectionKey).equals(BaseUtils.getPropertyString(BaseUtils.getClientName() + "_zipmd5")) ||
		!new File(binfolder + "natives").exists() || Frame.main.updatepr.isSelected()) { files.add(b+"/client.zip");  zipupdate = true; }
		if(!EncodingUtils.xorencode(EncodingUtils.inttostr(answer.split("<br>")[0].split("<:>")[2]), Settings.protectionKey).equals(GuardUtils.getMD5(binfolder + l))) files.add(b+"/"+l);
		if(!EncodingUtils.xorencode(EncodingUtils.inttostr(answer.split("<br>")[0].split("<:>")[3]), Settings.protectionKey).equals(GuardUtils.getMD5(binfolder + f))) files.add(b+"/"+f);
		if(!EncodingUtils.xorencode(EncodingUtils.inttostr(answer.split("<br>")[0].split("<:>")[4]), Settings.protectionKey).equals(GuardUtils.getMD5(binfolder + e))) files.add(b+"/"+e);
		if(!EncodingUtils.xorencode(EncodingUtils.inttostr(answer.split("<br>")[0].split("<:>")[1]), Settings.protectionKey).equals(GuardUtils.getMD5(binfolder + m))) files.add(b+"/"+m);

		
		BaseUtils.send("---- Filelist start ----");
		for(Object s : files.toArray())
		{
			BaseUtils.send("- " + (String) s);
		}
		BaseUtils.send("---- Filelist end ----");
		BaseUtils.send("Running updater...");
		updaterThread = new UpdaterThread(files, zipupdate, answer);
		updaterThread.setName("Updater thread");
		Frame.main.setUpdateState();
		updaterThread.run();
	}
	
	public static void pollSelectedServer()
	{
		try
		{
			serverPollThread.interrupt();
			serverPollThread = null;
		} catch (Exception e) {}
		
		BaseUtils.send("Refreshing server state... (" + Frame.main.servers.getSelected() + ")");
		serverPollThread = new Thread()
		{
			public void run()
			{
				Frame.main.serverbar.updateBar("����������...", BaseUtils.genServerIcon(new String[]{null, "0", null}));
				int sindex = Frame.main.servers.getSelectedIndex();
				String ip = Settings.servers[sindex].split(", ")[1];
				int port = BaseUtils.parseInt(Settings.servers[sindex].split(", ")[2], 25565);
				
				if(Frame.main.offline.isSelected())
				{
					Frame.main.serverbar.updateBar("������ �������", BaseUtils.genServerIcon(new String[]{null, "0", null}));
					return;
				}
				
				String[] status = BaseUtils.pollServer(ip, port);
				String text = BaseUtils.genServerStatus(status);
				BufferedImage img = BaseUtils.genServerIcon(status);
				Frame.main.serverbar.updateBar(text, img);
				
				serverPollThread.interrupt();
				serverPollThread = null;
				BaseUtils.send("Refreshing server done!");
			}
		};
		serverPollThread.setName("Server poll thread");
		serverPollThread.start();
	}

	public static void upload(final File file, final int type)
	{
		new Thread(){ public void run()
		{
			String answer = BaseUtils.execute(buildUrl("launcher.php"), new Object[]
			{
				"action", type > 0 ? "uploadcloak" : "uploadskin",
				"login", Frame.main.login.getText(),
				"password", new String(Frame.main.password.getPassword()),
				"ufile", file
			});
			boolean error = false;
			if(answer == null)
			{
				Frame.main.panel.tmpString = "������ �����������";
				error = true;
			} else if(!answer.contains("success"))
			{
				Frame.main.panel.tmpString = answer;
				error = true;
			} if(error)
			{
				Frame.main.panel.tmpColor = Color.red;
				try
				{
					sleep(2000);
				} catch (InterruptedException e) {}
				Frame.main.setPersonal(Frame.main.panel.pc);
				return;
			} else
			{
				if(type > 0)
				{
					Frame.main.panel.pc.realmoney = Integer.parseInt(answer.replaceAll("success:", BaseUtils.empty));
					Frame.main.panel.pc.cloak = ImageUtils.parseCloak(BaseUtils.getCloakImage(Frame.main.login.getText()));
				} else Frame.main.panel.pc.skin = ImageUtils.parseSkin(BaseUtils.getSkinImage(Frame.main.login.getText()));
				Frame.main.setPersonal(Frame.main.panel.pc);
			}
		}}.start();
	}
	
	public static void vaucher(final String vaucher)
	{
		new Thread(){ public void run()
		{
			String answer = BaseUtils.execute(buildUrl("launcher.php"), new Object[]
			{
				"action", "activatekey",
				"login", Frame.main.login.getText(),
				"password", new String(Frame.main.password.getPassword()),
				"key", vaucher,
			});
			boolean error = false;
			if(answer == null)
			{
				Frame.main.panel.tmpString = "������ �����������";
				error = true;
			} else if(!answer.contains("success"))
			{
				Frame.main.panel.tmpString = answer;
				error = true;
			} if(error)
			{
				Frame.main.panel.tmpColor = Color.red;
				try
				{
					sleep(2000);
				} catch (InterruptedException e) {}
				Frame.main.setPersonal(Frame.main.panel.pc);
				return;
			} else
			{
				Frame.main.panel.pc.realmoney = Integer.parseInt(answer.replaceAll("success:", BaseUtils.empty));
				Frame.main.setPersonal(Frame.main.panel.pc);
			}
		}}.start();
	}

	public static void exchange(final String text)
	{
		new Thread(){ public void run()
		{
			String answer = BaseUtils.execute(buildUrl("launcher.php"), new Object[]
			{
				"action", "exchange",
				"login", Frame.main.login.getText(),
				"password", new String(Frame.main.password.getPassword()),
				"buy", text,
			});
			boolean error = false;
			if(answer == null)
			{
				Frame.main.panel.tmpString = "������ �����������";
				error = true;
			} else if(!answer.contains("success"))
			{
				Frame.main.panel.tmpString = answer;
				error = true;
			} if(error)
			{
				Frame.main.panel.tmpColor = Color.red;
				try
				{
					sleep(2000);
				} catch (InterruptedException e) {}
				Frame.main.setPersonal(Frame.main.panel.pc);
				return;
			} else
			{
				String[] moneys = answer.replaceAll("success:", BaseUtils.empty).split(":");
				Frame.main.panel.pc.realmoney = Integer.parseInt(moneys[0]);
				Frame.main.panel.pc.iconmoney = Double.parseDouble(moneys[1]);
				Frame.main.vaucher.setText(BaseUtils.empty);
				Frame.main.setPersonal(Frame.main.panel.pc);
			}
		}}.start();
	}

	public static void buyVip(final int i)
	{
		new Thread(){ public void run()
		{
			String answer = BaseUtils.execute(buildUrl("launcher.php"), new Object[]
			{
				"action", i > 0 ? "buypremium" : "buyvip",
				"login", Frame.main.login.getText(),			
				"password", new String(Frame.main.password.getPassword()),
			});
			boolean error = false;
			if(answer == null)
			{
				Frame.main.panel.tmpString = "������ �����������";
				error = true;
			} else if(!answer.contains("success"))
			{
				Frame.main.panel.tmpString = answer;
				error = true;
			} if(error)
			{
				Frame.main.panel.tmpColor = Color.red;
				try
				{
					sleep(2000);
				} catch (InterruptedException e) {}
				Frame.main.setPersonal(Frame.main.panel.pc);
				return;
			} else
			{
				String[] data = answer.replaceAll("success:", BaseUtils.empty).split(":");
				Frame.main.panel.pc.realmoney = Integer.parseInt(data[0]);
				Frame.main.panel.pc.dateofexpire = BaseUtils.unix2hrd(Long.parseLong(data[1]));
				Frame.main.panel.pc.ugroup = i > 0 ? "Premium" : "VIP";
				Frame.main.setPersonal(Frame.main.panel.pc);
			}
		}}.start();
	}
	
	public static void unban()
	{
		new Thread(){ public void run()
		{
			String answer = BaseUtils.execute(buildUrl("launcher.php"), new Object[]
			{
				"action", "buyunban",
				"login", Frame.main.login.getText(),
				"password", new String(Frame.main.password.getPassword()),
			});
			boolean error = false;
			if(answer == null)
			{
				Frame.main.panel.tmpString = "������ �����������";
				error = true;
			} else if(!answer.contains("success"))
			{
				Frame.main.panel.tmpString = answer;
				error = true;
			} if(error)
			{
				Frame.main.panel.tmpColor = Color.red;
				try
				{
					sleep(2000);
				} catch (InterruptedException e) {}
				Frame.main.setPersonal(Frame.main.panel.pc);
				return;
			} else
			{
				String[] s = answer.split(":");
				Frame.main.panel.pc.ugroup = s[2];
				Frame.main.buyUnban.setEnabled(false);
				Frame.main.panel.pc.realmoney = Integer.parseInt(s[1]);
				Frame.main.setPersonal(Frame.main.panel.pc);
			}
		}}.start();
	}
}