// EMREditorPanel.java
package com.dw.gui.panel.other;

import com.dw.dao.other.EMRDao;
import com.dw.dao.record.MedicalRecordDao;
import com.dw.model.user.Doctor;
import com.dw.model.other.EMRSection;
import com.dw.model.record.MedicalRecord;
import com.dw.util.UIUtil;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class EMREditorPanel extends JPanel {
    private final Doctor doctor;
    private MedicalRecord currentRecord;
    private Map<String, JTextArea> sectionEditors = new HashMap<>();
    private JButton btnSave;
    private EMRDao emrDao = new EMRDao();

    public EMREditorPanel(Doctor doctor) {
        this.doctor = doctor;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        // 病历选择面板
        JPanel recordPanel = createRecordSelectionPanel();

        // 编辑区域
        JTabbedPane tabbedPane = createSectionTabs();

        // 操作按钮
        btnSave = UIUtil.createButton("保存病历");
        btnSave.addActionListener(this::saveEMR);

        add(recordPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
        add(btnSave, BorderLayout.SOUTH);
    }

    private JPanel createRecordSelectionPanel() {
        JPanel panel = UIUtil.createPanel();
        panel.setBorder(new TitledBorder("选择病历"));

        JTextField tfRecordId = UIUtil.createTextField();
        JButton btnLoad = UIUtil.createButton("加载病历");
        btnLoad.addActionListener(e -> loadRecord(tfRecordId.getText()));

        panel.add(UIUtil.createLabel("病历号："));
        panel.add(tfRecordId);
        panel.add(btnLoad);
        return panel;
    }

    private JTabbedPane createSectionTabs() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIUtil.NORMAL_FONT);

        String[] sections = {
                "主诉", "HISTORY_PRESENT",
                "现病史", "PHYSICAL_EXAM",
                "查体", "DIAGNOSIS"
        };

        for (int i = 0; i < sections.length; i += 2) {
            JTextArea editor = new JTextArea(15, 60);
            editor.setFont(UIUtil.NORMAL_FONT);
            sectionEditors.put(sections[i+1], editor);

            JScrollPane scrollPane = new JScrollPane(editor);
            tabbedPane.addTab(sections[i], scrollPane);
        }
        return tabbedPane;
    }

    private void loadRecord(String recordId) {
        try {
            int id = Integer.parseInt(recordId);
            currentRecord = new MedicalRecordDao().findById(id);
            loadSections();
            UIUtil.showInfo(this, "成功加载病历：" + id, "加载成功");
        } catch (NumberFormatException ex) {
            UIUtil.showError(this, "病历号必须为数字", "输入错误");
        } catch (SQLException ex) {
            UIUtil.showError(this, "加载失败：" + ex.getMessage(), "系统错误");
        }
    }

    private void loadSections() throws SQLException {
        List<EMRSection> sections = emrDao.getSections(currentRecord.getId());
        sections.forEach(section -> {
            JTextArea editor = sectionEditors.get(section.getSectionType());
            if (editor != null) {
                editor.setText(section.getContent());
            }
        });
    }

    private void saveEMR(ActionEvent e) {
        if (currentRecord == null) {
            UIUtil.showError(this, "请先选择病历", "保存失败");
            return;
        }

        try {
            saveAllSections();
            UIUtil.showInfo(this, "病历保存成功", "操作成功");
        } catch (SQLException ex) {
            UIUtil.showError(this, "保存失败：" + ex.getMessage(), "系统错误");
        }
    }

    private void saveAllSections() throws SQLException {
        for (Map.Entry<String, JTextArea> entry : sectionEditors.entrySet()) {
            EMRSection section = new EMRSection();
            section.setRecordId(currentRecord.getId());
            section.setSectionType(entry.getKey());
            section.setContent(entry.getValue().getText());
            emrDao.saveSection(section);
        }
    }
}