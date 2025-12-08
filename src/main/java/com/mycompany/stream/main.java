/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.stream;

import io.obswebsocket.community.client.OBSRemoteController;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

/**
 *
 * @author semih
 */
public class main extends javax.swing.JFrame implements NativeKeyListener {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(main.class.getName());

    private Logger log = null;
    private Properties prop = new Properties();
    private File propFile = null;
    private OBSRemoteController controller;

    private String scane1 = "", scane2 = "", scane3 = "", scane4 = "", scane5 = "";

    /**
     * Creates new form main
     */
    public main() {
        initComponents();
        this.setIconImage(new ImageIcon(getClass().getResource("/icon.png")).getImage());

        JOptionPane.showMessageDialog(null, "OBS Controller programına hoş geldiniz ve iyi yayınlar dileriz.\nhttps://github.com/semihkurucay", "SK Yazılım", -1);
        
        rememberMeMange();

        listenKey();

        scane1 = getPropValue("scane1");
        btn1.setText(scane1);

        scane2 = getPropValue("scane2");
        btn2.setText(scane2);

        scane3 = getPropValue("scane3");
        btn3.setText(scane3);

        scane4 = getPropValue("scane4");
        btn4.setText(scane4);

        scane5 = getPropValue("scane5");
        btn5.setText(scane5);

        controlerConnect();
    }

    private void listenKey() {
        log = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        log.setLevel(Level.OFF);
        log.setUseParentHandlers(false);

        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException ex) {
            JOptionPane.showMessageDialog(null, "Kalvye kısa yolları çalışmıyor.", "Kısa Yollar Çalışmıyor", JOptionPane.ERROR_MESSAGE);
        }

        GlobalScreen.addNativeKeyListener(this);
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        if (e.getKeyCode() == NativeKeyEvent.VC_F1 && (e.getModifiers() & NativeKeyEvent.CTRL_MASK) != 0) {
            changeScane(scane1);
        }

        if (e.getKeyCode() == NativeKeyEvent.VC_F2 && (e.getModifiers() & NativeKeyEvent.CTRL_MASK) != 0) {
            changeScane(scane2);
        }

        if (e.getKeyCode() == NativeKeyEvent.VC_F3 && (e.getModifiers() & NativeKeyEvent.CTRL_MASK) != 0) {
            changeScane(scane3);
        }

        if (e.getKeyCode() == NativeKeyEvent.VC_F4 && (e.getModifiers() & NativeKeyEvent.CTRL_MASK) != 0) {
            changeScane(scane4);
        }

        if (e.getKeyCode() == NativeKeyEvent.VC_F5 && (e.getModifiers() & NativeKeyEvent.CTRL_MASK) != 0) {
            changeScane(scane5);
        }

        if (e.getKeyCode() == NativeKeyEvent.VC_F9 && (e.getModifiers() & NativeKeyEvent.CTRL_MASK) != 0) {
            micUnmute();
        }

        if (e.getKeyCode() == NativeKeyEvent.VC_F10 && (e.getModifiers() & NativeKeyEvent.CTRL_MASK) != 0) {
            micMute();
        }

        if (e.getKeyCode() == NativeKeyEvent.VC_F11 && (e.getModifiers() & NativeKeyEvent.CTRL_MASK) != 0) {
            audioUnmute();
        }

        if (e.getKeyCode() == NativeKeyEvent.VC_F12 && (e.getModifiers() & NativeKeyEvent.CTRL_MASK) != 0) {
            audioMute();
        }
    }

    private void rememberMeMange() {
        File app = new File(System.getProperty("user.home") + File.separator + ".myApp");

        if (!app.exists()) {
            app.mkdirs();
        }

        propFile = new File(app, "obs.properties");

        if (!propFile.exists()) {
            createRememberMe();
        }

        loadRememberMe();
    }

    private void createRememberMe() {
        try {
            prop.setProperty("HOST", "localhost");
            prop.setProperty("PORT", "4455");
            prop.setProperty("PASSWORD", "");
            prop.setProperty("scane1", "");
            prop.setProperty("scane2", "");
            prop.setProperty("scane3", "");
            prop.setProperty("scane4", "");
            prop.setProperty("scane5", "");

            try (FileOutputStream fos = new FileOutputStream(propFile)) {
                prop.store(fos, "create");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Beni hatırla dosyası oluşturulurken hata çıktı!", "Beni Hatırla", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Beni hatırla dosyası oluşturulurken hata çıktı!", "Beni Hatırla", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadRememberMe() {
        try (FileInputStream fis = new FileInputStream(propFile)) {
            prop.load(fis);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Beni hatırla bilgileri getiremedi!", "Beni Hatırla", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void save(String key, String value) {
        prop.setProperty(key, value);

        try (FileOutputStream fop = new FileOutputStream(propFile)) {
            prop.store(fop, "rememberMe");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Beni hatırla kaydedilirken hata çıktı!", "Beni Hatırla", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getPropValue(String key) {
        return prop.getProperty(key) != null ? prop.getProperty(key) : "";
    }

    private void controlerConnect() {
        controller = OBSRemoteController.builder()
                .host(getPropValue("HOST"))
                .port(Integer.parseInt(getPropValue("PORT")))
                .password(getPropValue("PASSWORD"))
                .connectionTimeout(3)
                .lifecycle()
                .onReady(() -> {
                    this.setTitle("SK OBS Controller [OBS Bağlı]");
                    JOptionPane.showMessageDialog(null, "Bağlantı Başarılı!", "OBS Bağlanıldı", JOptionPane.INFORMATION_MESSAGE);
                })
                .onDisconnect(() -> {
                    this.setTitle("SK OBS Controller [OBS Bağlı Değil]");
                    JOptionPane.showMessageDialog(null, "Bağlantı Koptu!", "OBS Bağlanamadı", JOptionPane.ERROR_MESSAGE);
                })
                .and()
                .build();

        controller.connect();
    }

    private void changeScane(String scane) {
        if (controller == null) {
            JOptionPane.showMessageDialog(null, "OBS Bağlı değil, tekrar bağlanılıyor!\nTekrar deneyin!", "OBS Bağlanıyor", JOptionPane.WARNING_MESSAGE);
            controlerConnect();
            return;
        }

        controller.setCurrentProgramScene(scane, response -> {
            if (!response.isSuccessful()) {
                JOptionPane.showMessageDialog(null, "Sahne değişirken hata oluştu, sahne değişemedi!", "Sahne Değişmedi", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void micMute() {
        if (controller == null) {
            JOptionPane.showMessageDialog(null, "OBS Bağlı değil, tekrar bağlanılıyor!\nTekrar deneyin!", "OBS Bağlanıyor", JOptionPane.WARNING_MESSAGE);
            controlerConnect();
            return;
        }

        controller.setInputMute("Mic/Aux", true, response -> {
            if (!response.isSuccessful()) {
                JOptionPane.showMessageDialog(null, "Mikrafon sessize alınırken hata oluştu!", "Mikrafon Açık", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void micUnmute() {
        if (controller == null) {
            JOptionPane.showMessageDialog(null, "OBS Bağlı değil, tekrar bağlanılıyor!\nTekrar deneyin!", "OBS Bağlanıyor", JOptionPane.WARNING_MESSAGE);
            controlerConnect();
            return;
        }

        controller.setInputMute("Mic/Aux", false, response -> {
            if (!response.isSuccessful()) {
                JOptionPane.showMessageDialog(null, "Mikrafon ses açılırken hata oluştu!", "Mikrafon Kapalı", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void audioMute() {
        if (controller == null) {
            JOptionPane.showMessageDialog(null, "OBS Bağlı değil, tekrar bağlanılıyor!\nTekrar deneyin!", "OBS Bağlanıyor", JOptionPane.WARNING_MESSAGE);
            controlerConnect();
            return;
        }

        controller.setInputMute("Masaüstü Ses", true, response -> {
            if (!response.isSuccessful()) {
                JOptionPane.showMessageDialog(null, "Masaüstü ses sessize alınırken hata oluştu!", "Ses Açık", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void audioUnmute() {
        if (controller == null) {
            JOptionPane.showMessageDialog(null, "OBS Bağlı değil, tekrar bağlanılıyor!\nTekrar deneyin!", "OBS Bağlanıyor", JOptionPane.WARNING_MESSAGE);
            controlerConnect();
            return;
        }

        controller.setInputMute("Masaüstü Ses", false, response -> {
            if (!response.isSuccessful()) {
                JOptionPane.showMessageDialog(null, "Masaüstü ses açılırken hata oluştu!", "Ses Kapalı", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        btn1 = new javax.swing.JButton();
        btn2 = new javax.swing.JButton();
        btn3 = new javax.swing.JButton();
        btn4 = new javax.swing.JButton();
        lbl1 = new javax.swing.JLabel();
        lbl2 = new javax.swing.JLabel();
        lbl3 = new javax.swing.JLabel();
        lbl4 = new javax.swing.JLabel();
        btnMicUn = new javax.swing.JButton();
        btnMicMute = new javax.swing.JButton();
        btnAudUn = new javax.swing.JButton();
        btnAudMute = new javax.swing.JButton();
        btn5 = new javax.swing.JButton();
        lbl5 = new javax.swing.JLabel();
        btnObsSetting = new javax.swing.JButton();
        btnReconnect = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("SK OBS Controller [OBS Bağlı Değil]");
        setAlwaysOnTop(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(89, 89, 89));

        jPanel2.setBackground(new java.awt.Color(216, 201, 174));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(89, 89, 89));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("SK OBS Controller");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 65, Short.MAX_VALUE)
                .addContainerGap())
        );

        btn1.setToolTipText("(Ctrl +F1)");
        btn1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn1.setMaximumSize(new java.awt.Dimension(100, 100));
        btn1.setMinimumSize(new java.awt.Dimension(100, 100));
        btn1.setPreferredSize(new java.awt.Dimension(100, 100));
        btn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn1ActionPerformed(evt);
            }
        });

        btn2.setToolTipText("(Ctrl +F2)");
        btn2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn2.setMaximumSize(new java.awt.Dimension(100, 100));
        btn2.setMinimumSize(new java.awt.Dimension(100, 100));
        btn2.setPreferredSize(new java.awt.Dimension(100, 100));
        btn2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn2ActionPerformed(evt);
            }
        });

        btn3.setToolTipText("(Ctrl +F3)");
        btn3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn3.setMaximumSize(new java.awt.Dimension(100, 100));
        btn3.setMinimumSize(new java.awt.Dimension(100, 100));
        btn3.setPreferredSize(new java.awt.Dimension(100, 100));
        btn3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn3ActionPerformed(evt);
            }
        });

        btn4.setToolTipText("(Ctrl +F4)");
        btn4.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn4.setMaximumSize(new java.awt.Dimension(100, 100));
        btn4.setMinimumSize(new java.awt.Dimension(100, 100));
        btn4.setPreferredSize(new java.awt.Dimension(100, 100));
        btn4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn4ActionPerformed(evt);
            }
        });

        lbl1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl1.setForeground(new java.awt.Color(216, 201, 174));
        lbl1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl1.setText("Düzenle");
        lbl1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lbl1.setMaximumSize(new java.awt.Dimension(100, 20));
        lbl1.setMinimumSize(new java.awt.Dimension(100, 20));
        lbl1.setPreferredSize(new java.awt.Dimension(100, 20));
        lbl1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbl1MouseClicked(evt);
            }
        });

        lbl2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl2.setForeground(new java.awt.Color(216, 201, 174));
        lbl2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl2.setText("Düzenle");
        lbl2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lbl2.setMaximumSize(new java.awt.Dimension(100, 20));
        lbl2.setMinimumSize(new java.awt.Dimension(100, 20));
        lbl2.setPreferredSize(new java.awt.Dimension(100, 20));
        lbl2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbl2MouseClicked(evt);
            }
        });

        lbl3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl3.setForeground(new java.awt.Color(216, 201, 174));
        lbl3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl3.setText("Düzenle");
        lbl3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lbl3.setMaximumSize(new java.awt.Dimension(100, 20));
        lbl3.setMinimumSize(new java.awt.Dimension(100, 20));
        lbl3.setPreferredSize(new java.awt.Dimension(100, 20));
        lbl3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbl3MouseClicked(evt);
            }
        });

        lbl4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl4.setForeground(new java.awt.Color(216, 201, 174));
        lbl4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl4.setText("Düzenle");
        lbl4.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lbl4.setMaximumSize(new java.awt.Dimension(100, 20));
        lbl4.setMinimumSize(new java.awt.Dimension(100, 20));
        lbl4.setPreferredSize(new java.awt.Dimension(100, 20));
        lbl4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbl4MouseClicked(evt);
            }
        });

        btnMicUn.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnMicUn.setText("Mikrafon Aç");
        btnMicUn.setToolTipText("Mikrafon adı şöyle olamlı : Mic/Aux (Ctrl + F9)");
        btnMicUn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnMicUn.setMaximumSize(new java.awt.Dimension(125, 30));
        btnMicUn.setMinimumSize(new java.awt.Dimension(125, 30));
        btnMicUn.setPreferredSize(new java.awt.Dimension(125, 30));
        btnMicUn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMicUnActionPerformed(evt);
            }
        });

        btnMicMute.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnMicMute.setText("Mikrafon Kapa");
        btnMicMute.setToolTipText("Mikrafon adı şöyle olamlı : Mic/Aux (Ctrl + F10)");
        btnMicMute.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnMicMute.setMaximumSize(new java.awt.Dimension(125, 30));
        btnMicMute.setMinimumSize(new java.awt.Dimension(125, 30));
        btnMicMute.setPreferredSize(new java.awt.Dimension(125, 30));
        btnMicMute.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMicMuteActionPerformed(evt);
            }
        });

        btnAudUn.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnAudUn.setText("Ses Aç");
        btnAudUn.setToolTipText("Ses adı şöyle olamlı : Masaüstü Ses (Ctrl + F11)");
        btnAudUn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAudUn.setMaximumSize(new java.awt.Dimension(125, 30));
        btnAudUn.setMinimumSize(new java.awt.Dimension(125, 30));
        btnAudUn.setPreferredSize(new java.awt.Dimension(125, 30));
        btnAudUn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAudUnActionPerformed(evt);
            }
        });

        btnAudMute.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnAudMute.setText("Ses Kapa");
        btnAudMute.setToolTipText("Ses adı şöyle olamlı : Masaüstü Ses (Ctrl + F12)");
        btnAudMute.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAudMute.setMaximumSize(new java.awt.Dimension(125, 30));
        btnAudMute.setMinimumSize(new java.awt.Dimension(125, 30));
        btnAudMute.setPreferredSize(new java.awt.Dimension(125, 30));
        btnAudMute.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAudMuteActionPerformed(evt);
            }
        });

        btn5.setToolTipText("(Ctrl +F5)");
        btn5.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn5.setMaximumSize(new java.awt.Dimension(100, 100));
        btn5.setMinimumSize(new java.awt.Dimension(100, 100));
        btn5.setPreferredSize(new java.awt.Dimension(100, 100));
        btn5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn5ActionPerformed(evt);
            }
        });

        lbl5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl5.setForeground(new java.awt.Color(216, 201, 174));
        lbl5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl5.setText("Düzenle");
        lbl5.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lbl5.setMaximumSize(new java.awt.Dimension(100, 20));
        lbl5.setMinimumSize(new java.awt.Dimension(100, 20));
        lbl5.setPreferredSize(new java.awt.Dimension(100, 20));
        lbl5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbl5MouseClicked(evt);
            }
        });

        btnObsSetting.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnObsSetting.setText("OBS Ayar");
        btnObsSetting.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnObsSetting.setMaximumSize(new java.awt.Dimension(530, 30));
        btnObsSetting.setMinimumSize(new java.awt.Dimension(530, 30));
        btnObsSetting.setPreferredSize(new java.awt.Dimension(530, 30));
        btnObsSetting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnObsSettingActionPerformed(evt);
            }
        });

        btnReconnect.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnReconnect.setText("Yeniden Bağlan");
        btnReconnect.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnReconnect.setMaximumSize(new java.awt.Dimension(530, 30));
        btnReconnect.setMinimumSize(new java.awt.Dimension(530, 30));
        btnReconnect.setPreferredSize(new java.awt.Dimension(530, 30));
        btnReconnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReconnectActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnObsSetting, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(btnMicUn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnMicMute, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnAudUn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnAudMute, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(lbl1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btn1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(btn2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btn3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btn4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addComponent(lbl2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(lbl3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(lbl4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btn5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lbl5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(btnReconnect, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btn1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btn2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btn3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btn4, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btn5, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnMicMute, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnMicUn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAudMute, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAudUn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(btnObsSetting, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnReconnect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void lbl1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl1MouseClicked
        // TODO add your handling code here:
        scane1 = JOptionPane.showInputDialog(null, "1. Butona Tam Sahne Adınızı Girin", "Sahne Adı Güncelleme", JOptionPane.WARNING_MESSAGE);
        save("scane1", scane1);
        btn1.setText(scane1);
    }//GEN-LAST:event_lbl1MouseClicked

    private void lbl2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl2MouseClicked
        // TODO add your handling code here:
        scane2 = JOptionPane.showInputDialog(null, "2. Butona Tam Sahne Adınızı Girin", "Sahne Adı Güncelleme", JOptionPane.WARNING_MESSAGE);
        save("scane2", scane2);
        btn2.setText(scane2);
    }//GEN-LAST:event_lbl2MouseClicked

    private void lbl3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl3MouseClicked
        // TODO add your handling code here:
        scane3 = JOptionPane.showInputDialog(null, "3. Butona Tam Sahne Adınızı Girin", "Sahne Adı Güncelleme", JOptionPane.WARNING_MESSAGE);
        save("scane3", scane3);
        btn3.setText(scane3);
    }//GEN-LAST:event_lbl3MouseClicked

    private void lbl4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl4MouseClicked
        // TODO add your handling code here:
        scane4 = JOptionPane.showInputDialog(null, "4. Butona Tam Sahne Adınızı Girin", "Sahne Adı Güncelleme", JOptionPane.WARNING_MESSAGE);
        save("scane4", scane4);
        btn4.setText(scane4);
    }//GEN-LAST:event_lbl4MouseClicked

    private void lbl5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl5MouseClicked
        // TODO add your handling code here:
        scane5 = JOptionPane.showInputDialog(null, "5. Butona Tam Sahne Adınızı Girin", "Sahne Adı Güncelleme", JOptionPane.WARNING_MESSAGE);
        save("scane5", scane5);
        btn5.setText(scane5);
    }//GEN-LAST:event_lbl5MouseClicked

    private void btnObsSettingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnObsSettingActionPerformed
        // TODO add your handling code here:
        setting set = new setting(prop, propFile);
        set.setVisible(true);
    }//GEN-LAST:event_btnObsSettingActionPerformed

    private void btn1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn1ActionPerformed
        // TODO add your handling code here:
        changeScane(scane1);
    }//GEN-LAST:event_btn1ActionPerformed

    private void btn2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn2ActionPerformed
        // TODO add your handling code here:
        changeScane(scane2);
    }//GEN-LAST:event_btn2ActionPerformed

    private void btn3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn3ActionPerformed
        // TODO add your handling code here:
        changeScane(scane3);
    }//GEN-LAST:event_btn3ActionPerformed

    private void btn4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn4ActionPerformed
        // TODO add your handling code here:
        changeScane(scane4);
    }//GEN-LAST:event_btn4ActionPerformed

    private void btn5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn5ActionPerformed
        // TODO add your handling code here:
        changeScane(scane5);
    }//GEN-LAST:event_btn5ActionPerformed

    private void btnMicUnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMicUnActionPerformed
        // TODO add your handling code here:
        micUnmute();
    }//GEN-LAST:event_btnMicUnActionPerformed

    private void btnMicMuteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMicMuteActionPerformed
        // TODO add your handling code here:
        micMute();
    }//GEN-LAST:event_btnMicMuteActionPerformed

    private void btnAudUnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAudUnActionPerformed
        // TODO add your handling code here:
        audioUnmute();
    }//GEN-LAST:event_btnAudUnActionPerformed

    private void btnAudMuteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAudMuteActionPerformed
        // TODO add your handling code here:
        audioMute();
    }//GEN-LAST:event_btnAudMuteActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        controller.disconnect();
    }//GEN-LAST:event_formWindowClosing

    private void btnReconnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReconnectActionPerformed
        // TODO add your handling code here:
        controlerConnect();
    }//GEN-LAST:event_btnReconnectActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        // TODO add your handling code here:
        controller.disconnect();
    }//GEN-LAST:event_formWindowClosed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new main().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn1;
    private javax.swing.JButton btn2;
    private javax.swing.JButton btn3;
    private javax.swing.JButton btn4;
    private javax.swing.JButton btn5;
    private javax.swing.JButton btnAudMute;
    private javax.swing.JButton btnAudUn;
    private javax.swing.JButton btnMicMute;
    private javax.swing.JButton btnMicUn;
    private javax.swing.JButton btnObsSetting;
    private javax.swing.JButton btnReconnect;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel lbl1;
    private javax.swing.JLabel lbl2;
    private javax.swing.JLabel lbl3;
    private javax.swing.JLabel lbl4;
    private javax.swing.JLabel lbl5;
    // End of variables declaration//GEN-END:variables
}
