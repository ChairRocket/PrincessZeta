// RobotBuilder Version: 2.0
//
// This file was generated by RobotBuilder. It contains sections of
// code that are automatically generated and assigned by robotbuilder.
// These sections will be updated in the future when you export to
// Java from RobotBuilder. Do not put any code or make any change in
// the blocks indicating autogenerated code or it will be lost on an
// update. Deleting the comments indicating the section will prevent
// it from being updated in the future.

package frc.robot.subsystems;

import frc.robot.commands.*;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=IMPORTS
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=IMPORTS
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;


/**
 *
 */
public class Shooter extends Subsystem {

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTANTS

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTANTS

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS
private WPI_TalonSRX shooterDrive1;
private WPI_TalonSRX shooterDrive2;

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS

    private final static double SPEED_P_CONSTANT_TOP = .005;
    private final static double SPEED_I_CONSTANT_TOP = 0.0;
    private final static double SPEED_D_CONSTANT_TOP = 0.0;
    private final static double SPEED_F_CONSTANT_TOP = 0.0;

    private final static double SPEED_P_CONSTANT_BOTTOM = 5.0;
    private final static double SPEED_I_CONSTANT_BOTTOM = 0.0025;
    private final static double SPEED_D_CONSTANT_BOTTOM = 0.0;
    private final static double SPEED_F_CONSTANT_BOTTOM = 0.0;

    private final static double speedP_top = SPEED_P_CONSTANT_TOP;
    private final static double speedI_top = SPEED_I_CONSTANT_TOP;
    private final static double speedD_top = SPEED_D_CONSTANT_TOP;
    private final static double speedF_top = SPEED_F_CONSTANT_TOP;

    private final static double speedP_bottom = SPEED_P_CONSTANT_BOTTOM;
    private final static double speedI_bottom = SPEED_I_CONSTANT_BOTTOM;
    private final static double speedD_bottom = SPEED_D_CONSTANT_BOTTOM;
    private final static double speedF_bottom = SPEED_F_CONSTANT_BOTTOM;

    private final static double angleOfCamera = 0.176996; // (must be in radians)
    private final static double angleBallLeaves = 0.707; //Fake value ~45 degrees (must be in radians)
    private final static double heightOfCamera = 1.875; 
    private final static double heightOfGoal = 7.010416666666666667; 
    private final static double deltaHeight = (heightOfGoal - heightOfCamera);
    private final static double g = 9.81;

    private final static int PID_SLOT_SPEED_MODE = 0;
    private final int TIMEOUT_MS = 10;

    private static final int MAX_TICKS_PER_SEC_TOP = 119000;
    private static final int MAX_TICKS_PER_SEC_BOTTOM = 119000;

    public Shooter() {
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTOR
        shooterDrive1 = new WPI_TalonSRX(6);
        shooterDrive2 = new WPI_TalonSRX(29);
    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTORS

    }

    @Override
    public void initDefaultCommand() {
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DEFAULT_COMMAND

        setDefaultCommand(new Shooting());

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DEFAULT_COMMAND

        // Set the default command for a subsystem here.
        // setDefaultCommand(new MySpecialCommand());
        shooterDrive1.setNeutralMode(NeutralMode.Brake);
        shooterDrive2.setNeutralMode(NeutralMode.Brake);
    }

    @Override
    public void periodic() {
        // Put code here to be run every loop

    }

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CMDPIDGETTERS

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CMDPIDGETTERS

    // Put methods for controlling this subsystem
    // here. Call these from Commands.
    public void shoot(double rotation) {
        this.setPercentSpeedPIDTop(rotation);
        this.setPercentSpeedPIDBottom(rotation);
    }


    /*
        a1 = arctan((h2 - h1) / d - tan(a2)). This equation, with a known distance input, helps find the 
        mounted camera angle.
    */
    public double getCameraMountingAngle(double measuredDistance){
        NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");
        NetworkTableEntry ty = table.getEntry("ty");
        double radiansToTarget = ty.getDouble(0.0) * (Math.PI/180.0);

        //find the result of (h2-h1)/d
        double heightOverDistance = deltaHeight/measuredDistance;

        //find the result of tan(a2)
        double tangentOfAngle = Math.tan(radiansToTarget);

        // (h2-h1)/d - tan(a2) subtract two results for the tangent of the two sides
        double TangentOfSides = heightOverDistance - tangentOfAngle;

        //inverse of the tan to get the camera mounting angle in radians
        double cameraMountingAngleRadians = Math.atan(TangentOfSides);

        return cameraMountingAngleRadians;
    }
   
    

    public double getHorizontalDistance() {
        NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");
        NetworkTableEntry ty = table.getEntry("ty");
        double angleToTarget = ty.getDouble(0.0) * (Math.PI/180.0);
    
        double distance = deltaHeight / (Math.tan(angleOfCamera + angleToTarget));
        
        return distance;
    }

    // Calculates the speed needed
    public void shootAuto() {
        //See what the camera mounting angle is.
        SmartDashboard.putNumber("Mounting Angle of Camera", getCameraMountingAngle(15.04166666667));

        double x = getHorizontalDistance();
        SmartDashboard.putNumber("Distance", x);

        double deltaHeight = heightOfGoal - heightOfCamera;
        //The velocity
        double velocityRaw = Math.sqrt((Math.pow(x, 2) * g)/(2*Math.cos(angleBallLeaves) * (x*Math.sin(angleBallLeaves) - deltaHeight*Math.cos(angleBallLeaves))));
        SmartDashboard.putNumber("Velocity", velocityRaw);

        this.shoot(velocityRaw/30);
        SmartDashboard.putNumber("Encoder Values Of Shooter",shooterDrive1.getSelectedSensorVelocity());

        
    }
    public void initSpeedMode() {
        shooterDrive1.set(ControlMode.Velocity, 0);
        shooterDrive1.config_kP(PID_SLOT_SPEED_MODE, speedP_top, TIMEOUT_MS);
        shooterDrive1.config_kI(PID_SLOT_SPEED_MODE, speedI_top, TIMEOUT_MS);
        shooterDrive1.config_kD(PID_SLOT_SPEED_MODE, speedD_top, TIMEOUT_MS);
        shooterDrive1.config_kF(PID_SLOT_SPEED_MODE, speedF_top, TIMEOUT_MS);
        shooterDrive1.selectProfileSlot(PID_SLOT_SPEED_MODE, 0);

        shooterDrive2.set(ControlMode.Velocity, 0);
        shooterDrive2.config_kP(PID_SLOT_SPEED_MODE, speedP_bottom, TIMEOUT_MS);
        shooterDrive2.config_kI(PID_SLOT_SPEED_MODE, speedI_bottom, TIMEOUT_MS);
        shooterDrive2.config_kD(PID_SLOT_SPEED_MODE, speedD_bottom, TIMEOUT_MS);
        shooterDrive2.config_kF(PID_SLOT_SPEED_MODE, speedF_bottom, TIMEOUT_MS);
        shooterDrive2.selectProfileSlot(PID_SLOT_SPEED_MODE, 0);
    }

    public void stop() {
        shoot(0);
    }

    public void setPercentSpeedPIDTop(double setSpeed) {
        shooterDrive1.set(ControlMode.Velocity, MAX_TICKS_PER_SEC_TOP * setSpeed);
    }

    public void setPercentSpeedPIDBottom(double setSpeed) {
        shooterDrive2.set(ControlMode.Velocity, MAX_TICKS_PER_SEC_BOTTOM * setSpeed);
    }
}
