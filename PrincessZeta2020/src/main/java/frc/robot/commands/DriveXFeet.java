// RobotBuilder Version: 2.0
//
// This file was generated by RobotBuilder. It contains sections of
// code that are automatically generated and assigned by robotbuilder.
// These sections will be updated in the future when you export to
// Java from RobotBuilder. Do not put any code or make any change in
// the blocks indicating autogenerated code or it will be lost on an
// update. Deleting the comments indicating the section will prevent
// it from being updated in the future.

package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Robot;

/**
 *
 */
public class DriveXFeet extends Command {

    private static final double PIDTURN_P = .5;
    private static final double PIDTURN_I = 0.001;
    private static final double PIDTURN_D = 0.0002;
    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_DECLARATIONS
    private double m_feet;
    private double m_maxSpeed;
 
    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_DECLARATIONS
    public PIDController pidController;
    private double feetPerTicks = 1/10;
    private double initialEncoderPosition;
    private double driveSpeed;
    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTOR
    public DriveXFeet(double feet, double maxSpeed) {

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTOR
        pidController = new PIDController(PIDTURN_P, PIDTURN_I, PIDTURN_D);
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_SETTING
        m_feet = feet;
        m_maxSpeed = maxSpeed;

        // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_SETTING
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=REQUIRES
        requires(Robot.driveTrain);

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=REQUIRES
        
        initialEncoderPosition = Robot.driveTrain.getFrontLeftEncoderPosition();
    }

    public DriveXFeet(double feet){
        pidController = new PIDController(PIDTURN_P, PIDTURN_I, PIDTURN_D);

        m_feet = feet;
        m_maxSpeed = .5;
        requires(Robot.driveTrain); 

        initialEncoderPosition = Robot.driveTrain.getFrontLeftEncoderPosition();
    }

    // Called just before this Command runs the first time
    @Override
    protected void initialize() {
        pidController.setSetpoint(m_feet);
        pidController.enableContinuousInput(-m_maxSpeed, m_maxSpeed);
        pidController.setTolerance(.1);
    }

    // Called repeatedly when this Command is scheduled to run
    @Override
    protected void execute() {
        double frontLeftEncoderPosition = Robot.driveTrain.getFrontLeftEncoderPosition();
        double feetMoved = feetPerTicks * (frontLeftEncoderPosition - initialEncoderPosition);
        
        driveSpeed = pidController.calculate(feetMoved);
        
        Robot.driveTrain.drive(driveSpeed, driveSpeed);
        SmartDashboard.putNumber("DriveTrain frontLeftEncoder", Robot.driveTrain.getFrontLeftEncoderPosition());
    }

    // Make this return true when this Command no longer needs to run execute()
    @Override
    protected boolean isFinished() {
        return pidController.atSetpoint();
    }

    // Called once after isFinished returns true
    @Override
    protected void end() {
        Robot.driveTrain.stop();
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    @Override
    protected void interrupted() {
        Robot.driveTrain.stop();
    }
}
