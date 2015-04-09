/*
 * Copyright 2004,2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ws.commons.tcpmon;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.ws.commons.tcpmon.bookmark.*;

import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Proxy that sniffs and shows HTTP messages and responses, both SOAP and plain
 * HTTP.
 */

public class TCPMon extends JFrame {

	/**
	 * 
	 */
	AdminPane adminPane;
	private final BookmarkManager bookmarkManager;;
	private static final long serialVersionUID = -4327034756400669754L;
	private static final int SPANISH = 0;
	private static final int ENGLISH = 1;
	private static int lang;
	static URL helpURL;
	/**
	 * Field notebook
	 */
	private JTabbedPane notebook = null;

	/**
	 * Field STATE_COLUMN
	 */
	static final int STATE_COLUMN = 0;

	/**
	 * Field OUTHOST_COLUMN
	 */
	static final int OUTHOST_COLUMN = 3;

	/**
	 * Field REQ_COLUMN
	 */
	static final int REQ_COLUMN = 4;

	/**
	 * Field ELAPSED_COLUMN
	 */
	static final int ELAPSED_COLUMN = 5;

	/**
	 * Field DEFAULT_HOST
	 */
	static final String DEFAULT_HOST = "127.0.0.1";

	/**
	 * Field DEFAULT_PORT
	 */
	static final int DEFAULT_PORT = 80;

	/**
	 * Field DEFAULT_PORT
	 */

	static final int DEFAULT_LOCAL_PORT = 8080;

	/**
	 * Constructor
	 *
	 * @param listenPort
	 * @param targetHost
	 * @param targetPort
	 * @param embedded
	 */

	public TCPMon(int listenPort, String targetHost, int targetPort,
			boolean embedded) {

		super(getMessage("httptracer00", "TCPMon"));

		notebook = new JTabbedPane();
		bookmarkManager = new BookmarkManager(getBookmarkLocation());
		// this.setLayout(new BorderLayout());
		// this.getContentPane().add(notebook, BorderLayout.CENTER);
		this.getContentPane().add(notebook);
		this.setJMenuBar(createMenuBar());

		adminPane = new AdminPane(notebook, getMessage("admin00", "Admin"));
		if (listenPort != 0) {
			Listener l = null;
			if (targetHost == null) {
				l = new Listener(notebook, null, listenPort, targetHost,
						targetPort, true, null);
			} else {
				l = new Listener(notebook, null, listenPort, targetHost,
						targetPort, false, null);
			}

			notebook.setSelectedIndex(1);
			l.HTTPProxyHost = System.getProperty("http.proxyHost");
			if ((l.HTTPProxyHost != null) && l.HTTPProxyHost.equals("")) {
				l.HTTPProxyHost = null;
			}
			if (l.HTTPProxyHost != null) {
				String tmp = System.getProperty("http.proxyPort");
				if ((tmp != null) && tmp.equals("")) {
					tmp = null;
				}
				if (tmp == null) {
					l.HTTPProxyPort = 80;
				} else {
					l.HTTPProxyPort = Integer.parseInt(tmp);
				}
			}
		}
		if (!embedded) {
			this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		}

		notebook.setSelectedIndex(0);
		this.pack();
		this.setSize(1000, 700);
		this.setVisible(true);
	}

	/**
	 * Constructor
	 *
	 * @param listenPort
	 * @param targetHost
	 * @param targetPort
	 */
	public TCPMon(int listenPort, String targetHost, int targetPort) {
		this(listenPort, targetHost, targetPort, false);
	}

	/**
	 * set up the L&F
	 *
	 * @param nativeLookAndFeel
	 * @throws Exception
	 */
	private static void setupLookAndFeel(boolean nativeLookAndFeel)
			throws Exception {
		String classname = UIManager.getCrossPlatformLookAndFeelClassName();
		if (nativeLookAndFeel) {
			classname = UIManager.getSystemLookAndFeelClassName();
		}
		String lafProperty = System.getProperty("httptracer.laf", "");
		if (lafProperty.length() > 0) {
			classname = lafProperty;
		}
		try {
			UIManager.setLookAndFeel(classname);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	}

	/**
	 * this is our main method
	 *
	 * @param args
	 */
	public static void main(String[] args) {

		try {

			// switch between swing L&F here
			setupLookAndFeel(true);

			Object[] options = { "Espanol", "English" };
			lang = JOptionPane.showOptionDialog(null,
					"Seleccionar idioma / Select language",
					"Traducido por: Miguel", -1,
					JOptionPane.INFORMATION_MESSAGE, null, options, null);
			if (args.length == 3) {
				int p1 = Integer.parseInt(args[0]);
				int p2 = Integer.parseInt(args[2]);
				new TCPMon(p1, args[1], p2);
			} else if (args.length == 1) {
				int p1 = Integer.parseInt(args[0]);
				new TCPMon(p1, null, 0);
			} else if (args.length != 0) {
				System.err.println(getMessage("usage00", "Usage:")
						+ " TCPMon [listenPort targetHost targetPort]\n");
			} else {
				new TCPMon(0, null, 0);
			}
		} catch (Throwable exp) {
			exp.printStackTrace();
		}
	}

	/**
	 * Field messages
	 */
	private static ResourceBundle messages = null;

	/**
	 * Get the message with the given key. There are no arguments for this
	 * message.
	 *
	 * @param key
	 * @param defaultMsg
	 * @return string
	 */
	public static String getMessage(String key, String defaultMsg) {
		try {
			if (messages == null) {
				initializeMessages();
			}
			return messages.getString(key);
		} catch (Throwable t) {

			// If there is any problem whatsoever getting the internationalized
			// message, return the default.
			return defaultMsg;
		}
	}

	/**
	 * Load the resource bundle messages from the properties file. This is ONLY
	 * done when it is needed. If no messages are printed (for example, only
	 * Wsdl2java is being run in non- verbose mode) then there is no need to
	 * read the properties file.
	 */

	private static void initializeMessages() {

		switch (lang) {
		case (SPANISH):
			messages = ResourceBundle
					.getBundle("org.apache.ws.commons.tcpmon.tcpmones");
			TCPMon.helpURL = AdminPane.class
					.getResource("/res/tcpmontutoriales.html");
			break;
		case (ENGLISH):
			messages = ResourceBundle
					.getBundle("org.apache.ws.commons.tcpmon.tcpmon");
			TCPMon.helpURL = AdminPane.class
					.getResource("/res/tcpmontutorial.html");
			break;
		case (JOptionPane.CLOSED_OPTION):
			System.exit(0);
			break;
		}
	}

	public JMenuBar createMenuBar() {
		JMenuBar menuBar;
		JMenu menu;

		// Create the menu bar.
		menuBar = new JMenuBar();

		// Build the first menu.
		menu = new JMenu(TCPMon.getMessage("bookmark00", "Bookmarks"));
		menuBar.add(menu);

		// menu.add(createMenuBar());
		JMenuItem addBookmarkMenuItem = new JMenuItem(new AbstractAction(
				TCPMon.getMessage("addBookmark00", "Add a Bookmark")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				String bookmarkName = (String) JOptionPane.showInputDialog(
						TCPMon.this,
						TCPMon.getMessage("bookmarkName00", "Bookmark Name:"),
						TCPMon.getMessage("addBookmark00", "Add a Bookmark"),
						JOptionPane.YES_NO_OPTION, null, null,
						Strings.getRemoteHost() + ":" + Strings.getLocalPort());
				if (bookmarkName != null) {
					bookmarkManager.add(new Bookmark(bookmarkName, Strings
							.getLocalPort(), Strings.getRemoteHost(), Strings
							.getRemotePort(), Strings.isSslServerSelected()));
					reloadBookmarkInMenu(menu);
					repaint();
				}
			}
		});

		JMenu menuDelete = new JMenu(TCPMon.getMessage("deleteBookmark00",
				"Delete Bookmarks"));

		menu.add(addBookmarkMenuItem);
		menu.addSeparator();
		menu.add(menuDelete);
		// Update the menu
		reloadBookmarkInMenu(menu);

		return menuBar;

	}

	private void reloadBookmarkInMenu(JMenu bookmarkMenu) {

		JMenu menuDel = new JMenu(TCPMon.getMessage("deleteBookmark00",
				"Delete Bookmark"));
		JMenuItem menuEntry2 = null;
		JMenuItem menuEntry = null;

		// Clear previous entries, if any.
		while (bookmarkMenu.getItemCount() > 1) {
			bookmarkMenu.remove(bookmarkMenu.getItemCount() - 1);
		}

		bookmarkMenu.addSeparator();
		// Load the bookmarks into menu.
		List<Bookmark> bookmarks = bookmarkManager.list();
		if (bookmarks != null && bookmarks.size() != 0) {
			for (final Bookmark bookmark : bookmarks) {
				menuEntry = new JMenuItem(
						new AbstractAction(bookmark.getName()) {

							private static final long serialVersionUID = 1L;

							public void actionPerformed(ActionEvent e) {
								Strings.setLocalPort(bookmark.getLocalPort());
								Strings.setRemoteHost(bookmark.getRemoteHost());
								Strings.setRemotePort(bookmark.getRemotePort());
								Strings.setSslServerSelected(bookmark
										.isSslServer());

								adminPane.port.setText(Strings.getLocalPort());
								adminPane.host.setText(Strings.getRemoteHost());
								adminPane.tport.setText(Strings.getRemotePort());
								adminPane.sslServer.setSelected(Strings
										.isSslServerSelected());
							}

						});
				bookmarkMenu.add(menuEntry);
			}
		}
		if (bookmarks != null && bookmarks.size() != 0) {
			bookmarkMenu.addSeparator();
			for (final Bookmark bookmark : bookmarks) {
				menuEntry2 = new JMenuItem(new AbstractAction(
						bookmark.getName()) {

					private static final long serialVersionUID = 1L;

					public void actionPerformed(ActionEvent e) {
						bookmarkManager.delete(bookmark);
						reloadBookmarkInMenu(bookmarkMenu);
					}

				});
				menuDel.add(menuEntry2);
			}
			
			menuDel.addSeparator();
			menuDel.add(new JMenuItem(new AbstractAction(TCPMon.getMessage("deleteAll00", "Delete All")) {
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {

					bookmarkManager.deleteAll();
					reloadBookmarkInMenu(bookmarkMenu);
				}
			}));
			
			bookmarkMenu.add(menuDel);
		}
		if (Utils.isEmptyOrNull(bookmarks)) {
			JMenuItem noBookmark = new JMenuItem(TCPMon.getMessage(
					"bookmarkNo00", "No Bookmark"));
			noBookmark.setEnabled(false);
			bookmarkMenu.add(noBookmark);
		}

	}

	public String getBookmarkLocation() {
		try {
			return System.getProperty("user.home")
					+ System.getProperty("file.separator") + ".tcpmon"
					+ System.getProperty("file.separator") + "bookmarks.txt";
		} catch (SecurityException e) {
		}

		return null;
	}

	public static int getLang() {
		return lang;
	}

	public static void setLang(int lang) {
		TCPMon.lang = lang;
	}
}
