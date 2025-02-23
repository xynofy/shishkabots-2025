package frc.robot.subsystems;

import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.config.ClosedLoopConfig.FeedbackSensor;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;

import frc.robot.Constants.ModuleConstants;

public class Configs {
    public static final class SwerveModule {
        // Make these public so they can be accessed from SwerveModule.java
        public static final SparkMaxConfig drivingConfig = new SparkMaxConfig();
        public static final SparkMaxConfig drivingInvertedConfig = new SparkMaxConfig();
        public static final SparkMaxConfig turningConfig = new SparkMaxConfig();

        public static void setDriveMotorSettings(SparkMaxConfig driveConfig, boolean inverted) {
            driveConfig
                .inverted(inverted ? true: false)
                .idleMode(IdleMode.kBrake)
                .smartCurrentLimit(40);
            driveConfig.encoder
                .positionConversionFactor(ModuleConstants.ROTATIONS_TO_METERS)
                .velocityConversionFactor(ModuleConstants.RPM_TO_MPS); // rotations per minute to MPS
            driveConfig.closedLoop
                .feedbackSensor(FeedbackSensor.kPrimaryEncoder)
                .pid(0.04, 0, 0)
                .velocityFF(ModuleConstants.DRIVE_VELOCITY_FEEDFOWARD)
                .outputRange(-1, 1);
        }
        public static void setTurningMotorSettings(SparkMaxConfig turnConfig) {
            turnConfig
                .idleMode(IdleMode.kBrake)
                .smartCurrentLimit(19);
            turnConfig.absoluteEncoder
                .inverted(true)
                .positionConversionFactor(ModuleConstants.ROTATIONS_TO_RADIANS)
                .velocityConversionFactor(ModuleConstants.RPM_TO_RADPS);
            turnConfig.closedLoop
                .feedbackSensor(FeedbackSensor.kAbsoluteEncoder)
                .pid(1, 0, 0)
                .velocityFF(0)
                .outputRange(-1, 1)
                .positionWrappingEnabled(true)
                .positionWrappingInputRange(0, ModuleConstants.ROTATIONS_TO_RADIANS);
        }
        static {
            setDriveMotorSettings(drivingConfig, false);
            setDriveMotorSettings(drivingInvertedConfig, true);
            setTurningMotorSettings(turningConfig);
        }
    }

    public static final class Elevator {
        public static final SparkMaxConfig motorConfig = new SparkMaxConfig();

        static {
            motorConfig
                .idleMode(IdleMode.kBrake)
                .smartCurrentLimit(40);
            
            motorConfig.encoder
                .positionConversionFactor(1.0)
                .velocityConversionFactor(1.0);
            
            motorConfig.closedLoop
                .feedbackSensor(FeedbackSensor.kPrimaryEncoder)
                .pid(0.1, 0.0, 0.0)
                .outputRange(-1, 1);
        }
    }
}