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
import edu.wpi.first.wpiutil.math.MathUtil;
import frc.robot.Robot;

/**
 *
 */
public class TurnNDegreesAbsolute extends Command {

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_DECLARATIONS
    private double m_degrees;
 
    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_DECLARATIONS


    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTOR
    public TurnNDegreesAbsolute(double degrees) {

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTOR
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_SETTING
        m_degrees = degrees;

        // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_SETTING
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=REQUIRES
        requires(Robot.driveTrain);

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=REQUIRES
        
        if(m_degrees > 180 || m_degrees < -180){
            throw new IllegalArgumentException();
        }
    }

    // Called just before this Command runs the first time
    @Override
    protected void initialize() {
        Robot.shifter.shiftLow();
        // TODO: Time how long it'll take in low gear to complete a 180 degree turn. That'll be the max.
        setTimeout(3);
        Robot.driveTrain.setTurnSetpoint(m_degrees);    
    }

    // Called repeatedly when this Command is scheduled to run
    @Override
    protected void execute() {
        double driveSpeed = Robot.driveTrain.turnCalculate(Robot.driveTrain.getGyroTurnAngle());
        driveSpeed = MathUtil.clamp(driveSpeed, -.4, .4);
        Robot.driveTrain.driveRaw(driveSpeed, -driveSpeed);
    }

    // Make this return true when this Command no longer needs to run execute()
    @Override
    protected boolean isFinished() { 
        return isTimedOut() || Robot.driveTrain.turnAtSetpoint();
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
        end();
    }
}
