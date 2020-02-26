package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.ControlType;
import com.revrobotics.CANSparkMax.IdleMode;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import com.revrobotics.CANPIDController;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.revrobotics.CANEncoder;
import frc.robot.Constants;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.AnalogInput;

public class DriveTrain{
   
    public CANSparkMax FRM;
    public CANSparkMax RRM;
    public CANSparkMax FLM;
    public CANSparkMax RLM;
    public SpeedControllerGroup m_rightDrive;
    public SpeedControllerGroup m_leftDrive;
    public DifferentialDrive m_driveTrain;
    public CANPIDController m_rightSidePID;
    public CANPIDController m_leftSidePID;
    public CANEncoder m_rightSideEncoder;
    public CANEncoder m_leftSideEncoder;

    public AnalogInput m_ultrasonic;

    double rotations = 0;


    public DriveTrain (){
        FRM = new CANSparkMax(Constants.driveTrain.FRMPort, MotorType.kBrushless);
        RRM = new CANSparkMax(Constants.driveTrain.RRMPort, MotorType.kBrushless);
        m_rightDrive = new SpeedControllerGroup(FRM, RRM);
        RLM = new CANSparkMax(Constants.driveTrain.RLMPort, MotorType.kBrushless);
        FLM = new CANSparkMax(Constants.driveTrain.FLMPort, MotorType.kBrushless);
        m_leftDrive = new SpeedControllerGroup(FLM, RLM);

        // FRM.restoreFactoryDefaults();
        // RRM.restoreFactoryDefaults();
        // RLM.restoreFactoryDefaults();
        // FLM.restoreFactoryDefaults();

        m_rightSideEncoder = new CANEncoder(FRM);
        // m_rightSidePID = new CANPIDController(FRM);
        // m_leftSidePID = new CANPIDController(FLM);
        m_leftSideEncoder = new CANEncoder(FLM);
        m_driveTrain = new DifferentialDrive(m_leftDrive, m_rightDrive);
        m_driveTrain.setRightSideInverted(true);
        m_driveTrain.setDeadband(.1);

        // we might be initializing pid stuff wrong!
        // below is new way to do PID which i think is actually right
        m_rightSidePID = FRM.getPIDController();
        m_leftSidePID = FLM.getPIDController();
        
        m_leftSidePID.setOutputRange(-1, 1);
        m_leftSidePID.setFF(.000167);
        m_leftSidePID.setP(0);
        m_leftSidePID.setI(0);
        m_leftSidePID.setD(0);
    
        m_rightSidePID.setOutputRange(-1, 1);
        m_rightSidePID.setFF(.000162);
        m_rightSidePID.setP(0); // .0001
        m_rightSidePID.setI(0);
        m_rightSidePID.setD(0); // .00035

        m_leftSideEncoder.setPosition(0);
        
        // m_ultrasonic = new AnalogInput(Constants.driveTrain.ultrasonicPort);


    }

    public void drive(double a1, double a2){
        // comment one of these drivetrains out or bad things will happen
        // m_leftSidePID.setReference(a1, ControlType.kVelocity);
        // m_rightSidePID.setReference(a2, ControlType.kVelocity);
        m_driveTrain.tankDrive(-a1, -a2);

    }

    public void arcadeDrive (double a1, double a2) {
        System.out.println("max rpm left " + m_leftSideEncoder.getVelocity());
        System.out.println("max rpm right " + m_rightSideEncoder.getVelocity());
        if (Math.abs(a2) >= 0)
            m_driveTrain.arcadeDrive(0, a2);
        if (Math.abs(a1) >= .05) {

            m_leftSidePID.setReference(-a1 * Constants.driveTrain.maxRPM, ControlType.kVelocity);
            m_rightSidePID.setReference(a1 * Constants.driveTrain.maxRPM, ControlType.kVelocity);
        }

        
        

    }
    
    public double encoderValue() {
        rotations = Math.abs((m_leftSideEncoder.getPosition()) + Math.abs((m_rightSideEncoder.getPosition()))) / 2;
        System.out.println("Encoder position is: " + m_leftSideEncoder.getPosition());
        return rotations;
    }

    public void zeroEncoder() {
        m_leftSideEncoder.setPosition(0);
        m_rightSideEncoder.setPosition(0);
    }

    public double readDistance() {
        double ultrasonicSensorValue = m_ultrasonic.getVoltage();
        final double scaleFactor = 1 / (5. / 1024.);
        double distance = 5 * ultrasonicSensorValue * scaleFactor;
        double convertedValue = distance / (305);

        return convertedValue;
    }

    public void brakeMode() {
        FLM.setIdleMode(IdleMode.kBrake);
        FRM.setIdleMode(IdleMode.kBrake);
        RRM.setIdleMode(IdleMode.kBrake);
        RLM.setIdleMode(IdleMode.kBrake);
    }

    public void coastMode() {
        FLM.setIdleMode(IdleMode.kCoast);
        FRM.setIdleMode(IdleMode.kCoast);
        RRM.setIdleMode(IdleMode.kCoast);
        RLM.setIdleMode(IdleMode.kCoast);
    }
}