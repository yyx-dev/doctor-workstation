package com.dw.gui;

import com.dw.dao.AppointmentDao;
import com.dw.dao.MedicalRecordDao;
import com.dw.dao.PatientDao;
import com.dw.model.Appointment;
import com.dw.model.Doctor;
import com.dw.model.MedicalRecord;
import com.dw.model.Patient;
import com.dw.util.UIUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

/**
 * 诊断对话框
 */
public class DiagnosisDialog extends JDialog {

    private Appointment appointment; // 挂号信息
    private Doctor doctor; // 医生信息
    private Patient patient; // 患者信息

    // 表单字段
    private JTextField patientNameField;
    private JTextField genderField;
    private JTextField ageField;
    private JTextArea chiefComplaintArea;
    private JTextArea diagnosisArea;
    private JTextArea prescriptionArea;

    // 按钮
    private JButton submitButton;
    private JButton cancelButton;

    // 数据访问对象
    private PatientDao patientDao = new PatientDao();
    private AppointmentDao appointmentDao = new AppointmentDao();
    private MedicalRecordDao medicalRecordDao = new MedicalRecordDao();

    /**
     * 构造函数
     * @param parent 父窗口
     * @param appointment 挂号信息
     * @param doctor 医生信息
     */
    public DiagnosisDialog(Window parent, Appointment appointment, Doctor doctor) {
        super(parent, "患者诊断", ModalityType.APPLICATION_MODAL);
        this.appointment = appointment;
        this.doctor = doctor;

        // 获取患者信息
        this.patient = patientDao.findById(appointment.getPatientId());

        // 初始化界面
        initUI();

        // 添加事件监听器
        addListeners();

        // 设置窗口属性
        setSize(600, 650);
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    /**
     * 初始化界面
     */
    private void initUI() {
        // 设置布局
        setLayout(new BorderLayout());

        // 创建内容面板
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 创建患者信息面板
        JPanel patientInfoPanel = new JPanel(new GridBagLayout());
        patientInfoPanel.setBackground(Color.WHITE);
        patientInfoPanel.setBorder(BorderFactory.createTitledBorder("患者信息"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);

        // 患者姓名
        JLabel patientNameLabel = UIUtil.createLabel("姓名：");
        patientNameField = UIUtil.createTextField();
        patientNameField.setText(patient.getName());
        patientNameField.setEditable(false);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        patientInfoPanel.add(patientNameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        patientInfoPanel.add(patientNameField, gbc);

        // 性别
        JLabel genderLabel = UIUtil.createLabel("性别：");
        genderField = UIUtil.createTextField();
        genderField.setText(patient.getGender());
        genderField.setEditable(false);

        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        patientInfoPanel.add(genderLabel, gbc);

        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        patientInfoPanel.add(genderField, gbc);

        // 年龄
        JLabel ageLabel = UIUtil.createLabel("年龄：");
        ageField = UIUtil.createTextField();
        ageField.setText(String.valueOf(patient.getAge()));
        ageField.setEditable(false);

        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        patientInfoPanel.add(ageLabel, gbc);

        gbc.gridx = 5;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        patientInfoPanel.add(ageField, gbc);

        // 挂号时间
        JLabel timeLabel = UIUtil.createLabel("挂号时间：");
        JTextField timeField = UIUtil.createTextField();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        timeField.setText(dateFormat.format(appointment.getAppointmentTime()));
        timeField.setEditable(false);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        patientInfoPanel.add(timeLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 5;
        patientInfoPanel.add(timeField, gbc);

        // 创建主诉面板
        JPanel complaintPanel = new JPanel(new BorderLayout());
        complaintPanel.setBackground(Color.WHITE);
        complaintPanel.setBorder(BorderFactory.createTitledBorder("患者主诉"));

        chiefComplaintArea = new JTextArea();
        chiefComplaintArea.setRows(5);
        chiefComplaintArea.setLineWrap(true);
        chiefComplaintArea.setWrapStyleWord(true);
        chiefComplaintArea.setText(appointment.getChiefComplaint());
        chiefComplaintArea.setEditable(false);
        chiefComplaintArea.setFont(UIUtil.NORMAL_FONT);

        JScrollPane complaintScrollPane = new JScrollPane(chiefComplaintArea);
        complaintScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        complaintPanel.add(complaintScrollPane, BorderLayout.CENTER);

        // 创建诊断面板
        JPanel diagnosisPanel = new JPanel(new BorderLayout());
        diagnosisPanel.setBackground(Color.WHITE);
        diagnosisPanel.setBorder(BorderFactory.createTitledBorder("诊断结果"));

        diagnosisArea = new JTextArea();
        diagnosisArea.setRows(6);
        diagnosisArea.setLineWrap(true);
        diagnosisArea.setWrapStyleWord(true);
        diagnosisArea.setFont(UIUtil.NORMAL_FONT);

        JScrollPane diagnosisScrollPane = new JScrollPane(diagnosisArea);
        diagnosisScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        diagnosisPanel.add(diagnosisScrollPane, BorderLayout.CENTER);

        // 创建药方面板
        JPanel prescriptionPanel = new JPanel(new BorderLayout());
        prescriptionPanel.setBackground(Color.WHITE);
        prescriptionPanel.setBorder(BorderFactory.createTitledBorder("药方"));

        prescriptionArea = new JTextArea();
        prescriptionArea.setRows(6);
        prescriptionArea.setLineWrap(true);
        prescriptionArea.setWrapStyleWord(true);
        prescriptionArea.setFont(UIUtil.NORMAL_FONT);

        JScrollPane prescriptionScrollPane = new JScrollPane(prescriptionArea);
        prescriptionScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        prescriptionPanel.add(prescriptionScrollPane, BorderLayout.CENTER);

        // 创建按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        submitButton = UIUtil.createButton("提交诊断");
        cancelButton = UIUtil.createButton("取消");

        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);

        // 组装所有面板
        JPanel centerPanel = new JPanel(new GridLayout(4, 1, 0, 10));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(patientInfoPanel);
        centerPanel.add(complaintPanel);
        centerPanel.add(diagnosisPanel);
        centerPanel.add(prescriptionPanel);

        contentPanel.add(centerPanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        // 添加到对话框
        add(contentPanel);
    }

    /**
     * 添加事件监听器
     */
    private void addListeners() {
        // 提交诊断按钮点击事件
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitDiagnosis();
            }
        });

        // 取消按钮点击事件
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // 关闭对话框
            }
        });
    }

    /**
     * 提交诊断结果
     */
    private void submitDiagnosis() {
        String diagnosis = diagnosisArea.getText();
        String prescription = prescriptionArea.getText();

        // 创建病历对象
        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setAppointmentId(appointment.getId());
        medicalRecord.setDoctorId(doctor.getId());
        medicalRecord.setPatientId(patient.getId());
        medicalRecord.setDiagnosis(diagnosis);
        medicalRecord.setPrescription(prescription);

        try {
            // 保存病历信息
            medicalRecordDao.add(medicalRecord);
            UIUtil.showInfo(this, "病历保存成功", "操作成功");
        } catch (SQLException ex) {
            UIUtil.showError(this, "保存病历失败：" + ex.getMessage(), "数据库错误");
            ex.printStackTrace();
        }

        // 更新挂号状态
        appointment.setStatus("completed");
        appointmentDao.update(appointment);

        UIUtil.showInfo(this, "诊断提交成功！", "成功");
        dispose(); // 关闭对话框
    }
}