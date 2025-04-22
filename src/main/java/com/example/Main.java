package com.example;
import javax.swing.*;
import javax.swing.event.AncestorListener;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Note Pad");
        frame.setBounds(100, 100, 800, 600);
        Image icon = Toolkit.getDefaultToolkit().getImage("icon.png");
        if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            try {
                Taskbar taskbar = Taskbar.getTaskbar();
                Image dockIcon = Toolkit.getDefaultToolkit().getImage(Main.class.getResource("/icon.png"));
                taskbar.setIconImage(dockIcon);
            } catch (UnsupportedOperationException | NullPointerException e) {
                e.printStackTrace();
            }
        }
        frame.setIconImage(icon);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        JTabbedPane tabbedPane = new JTabbedPane();
        frame.add(tabbedPane);
        createNewTab(tabbedPane, "未命名", "");
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e){
                int value=JOptionPane.showConfirmDialog(frame, "你真的要退出吗？", "退出程序", JOptionPane.YES_NO_OPTION);
                if(value == JOptionPane.OK_OPTION)    //返回值就是用户的选择结果，也是预置好的，这里判断如果是OK那么就退出
                    System.exit(0);
            }
        });
        JMenuBar bar = new JMenuBar();
        JMenu file = new JMenu("文件");
        JMenuItem newfile=new JMenuItem("新建");
        newfile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        file.add(newfile)
                .addActionListener(e -> createNewTab(tabbedPane, "未命名", ""));
        JMenuItem open=new JMenuItem("打开");
       open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        file.add(open)
                .addActionListener(e -> {
                    JFileChooser chooser = new JFileChooser();
                    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    int result = chooser.showOpenDialog(frame);
                    if (result == JFileChooser.APPROVE_OPTION) {
                        File fileChosen = chooser.getSelectedFile();
                        try {
                            Path path = fileChosen.toPath();
                            String content = Files.readString(path);
                            createNewTab(tabbedPane, fileChosen.getName(), content);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(frame, "读取文件失败：" + ex.getMessage());
                            ex.printStackTrace();
                        }
                    }
                });
        JMenuItem save=new JMenuItem("保存");
        save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        file.add(save)
                .addActionListener(e -> {
                    if (tabbedPane.getSelectedComponent() == null) return;
                    JTextArea currentTextArea = (JTextArea) ((JScrollPane)tabbedPane.getSelectedComponent()).getViewport().getView();
                    JFileChooser chooser = new JFileChooser();
                    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    int result = chooser.showSaveDialog(frame);
                    if (result == JFileChooser.APPROVE_OPTION) {
                        File fileChosen = chooser.getSelectedFile();
                        try {
                            Path path = fileChosen.toPath();
                            String content = currentTextArea.getText();
                            Files.writeString(path, content);
                            tabbedPane.setTitleAt(tabbedPane.getSelectedIndex(), fileChosen.getName());
                            JOptionPane.showMessageDialog(frame, "保存成功！");
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(frame, "保存文件失败：" + ex.getMessage());
                            ex.printStackTrace();
                        }
                    }
                });
        JMenuItem close=new JMenuItem("关闭标签页");
        close.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_DOWN_MASK));
        file.add(close)
                .addActionListener(e -> {
                    int selectedIndex = tabbedPane.getSelectedIndex();
                    if (selectedIndex != -1) {
                        tabbedPane.remove(selectedIndex);
                    }
                    if (tabbedPane.getTabCount() == 0) {
                        createNewTab(tabbedPane, "未命名", "");
                    }
                });
        bar.add(file);
        frame.setJMenuBar(bar);
        frame.setVisible(true);
    }
    private static void createNewTab(JTabbedPane tabbedPane, String title, String content) {
        JTextArea textArea = new JTextArea();
        textArea.setText(content);
        JScrollPane scrollPane = new JScrollPane(textArea);
        tabbedPane.addTab(title, scrollPane);
        tabbedPane.setSelectedComponent(scrollPane);
    }
}