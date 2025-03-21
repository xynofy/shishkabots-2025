// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.auto.NamedCommands;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import frc.robot.commands.DefaultDriveCommand;
import frc.robot.commands.LimelightDebugCommand;
import frc.robot.commands.TestAllCoralPos;
import frc.robot.commands.ElevatorTestCommand;
import frc.robot.commands.FineTuneShooterIntakeCommand;
import frc.robot.commands.ShootCommand;
import frc.robot.commands.PrepareShooterCommand;
import frc.robot.commands.CalibrateElevatorCommand;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.ElevatorSubsystem;
import frc.robot.subsystems.LimelightSubsystem;
import frc.robot.subsystems.ShooterSubsystem;
import frc.robot.subsystems.ShooterSubsystem.ShooterState;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj.PS4Controller;
import edu.wpi.first.wpilibj.XboxController;

public class RobotContainer {
  // The robot's subsystems
  private final ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem(
        Constants.ElevatorConstants.ELEVATOR_PRIMARY_MOTOR_ID,
        Constants.ElevatorConstants.ELEVATOR_SECONDARY_MOTOR_ID,
        Constants.ElevatorConstants.ELEVATOR_TOP_LIMIT_SWITCH_ID,
        Constants.ElevatorConstants.ELEVATOR_BOTTOM_LIMIT_SWITCH_ID
    );
  private final ShooterSubsystem shooterSubsystem = new ShooterSubsystem(
    Constants.ShooterConstants.SHOOTER_PRIMARY_MOTOR_ID,
    Constants.ShooterConstants.SHOOTER_SECONDARY_MOTOR_ID);

  private final LimelightSubsystem limelightSubsystem = new LimelightSubsystem();
  private final DriveSubsystem driveSubsystem = new DriveSubsystem(limelightSubsystem);

  // The driver's controllers
  private final XboxController xboxController = new XboxController(1);
  private final PS4Controller ps4Controller = new PS4Controller(0);

  // Set which controller to use (true for Xbox, false for PS4)
  private final boolean useXboxController = true;

  private static final double DEADBAND = 0.1;
  
  // setup the AutoBuilder with all pathplanner paths in place
  private final SendableChooser<Command> autoChooser;


  public LimelightSubsystem getLimelightSubsystem() {
    return limelightSubsystem;
  }
  private double applyDeadband(double value) {
    if (Math.abs(value) < DEADBAND) {
      return 0.0;
    }
    return value;
  }

  private double getForwardInput() {
    double raw = useXboxController ? -xboxController.getLeftY() : -ps4Controller.getLeftY();
    return applyDeadband(raw);
  }

  private double getStrafeInput() {
    double raw = useXboxController ? -xboxController.getLeftX() : -ps4Controller.getLeftX();
    return applyDeadband(raw);
  }

  private double getRotationInput() {
    double raw = useXboxController ? -xboxController.getRightX() : -ps4Controller.getRightX();
    return applyDeadband(raw);
  }

  public RobotContainer() {
    configureBindings();
    // Set up the default command for the drive subsystem
    driveSubsystem.setDefaultCommand(
        new DefaultDriveCommand(
            driveSubsystem,
            () -> getForwardInput() * 0.5,  // Forward/backward
            () -> getStrafeInput() * 0.5,   // Left/right
            () -> getRotationInput() * 0.5  // Rotation
        )
    );

    autoChooser = AutoBuilder.buildAutoChooser("shish-test");

      // Register Named Commands for Auton Routines
    NamedCommands.registerCommand("shootBottomLevel", new ShootCommand(shooterSubsystem, elevatorSubsystem));
    NamedCommands.registerCommand("prepareShooter", new PrepareShooterCommand(shooterSubsystem));
  }

  private void configureBindings() {
    // Xbox Controller Bindings
    if (useXboxController) {
      new JoystickButton(xboxController, XboxController.Button.kA.value)
          .onTrue(new ElevatorTestCommand(elevatorSubsystem, 1));
      new JoystickButton(xboxController, XboxController.Button.kB.value)
          .onTrue(new ElevatorTestCommand(elevatorSubsystem, 2));
      new JoystickButton(xboxController, XboxController.Button.kY.value)
          .onTrue(new ElevatorTestCommand(elevatorSubsystem, 3));
      // Use Left Bumper for level 0 (more reliable than POV button)
      new JoystickButton(xboxController, XboxController.Button.kX.value)
          .onTrue(new ElevatorTestCommand(elevatorSubsystem, 0));
      /*new JoystickButton(xboxController, XboxController.Button.kX.value)
          .whileTrue(new LimelightDebugCommand(limelightSubsystem));*/
      new JoystickButton(xboxController, XboxController.Button.kLeftBumper.value)
          .onTrue(Commands.either(
              new FineTuneShooterIntakeCommand(shooterSubsystem),
              new PrepareShooterCommand(shooterSubsystem),
              () -> shooterSubsystem.getState() == ShooterState.CORAL_INSIDE
          ));

      // Add binding for elevator calibration (Back/Select button)
      new JoystickButton(xboxController, XboxController.Button.kBack.value)
          .onTrue(new CalibrateElevatorCommand(elevatorSubsystem));

      // Shooter control - Right Bumper
      new JoystickButton(xboxController, XboxController.Button.kRightBumper.value)
          .onTrue(new ShootCommand(shooterSubsystem, elevatorSubsystem));
          
      // Emergency stop for elevator (Start button)
      new JoystickButton(xboxController, XboxController.Button.kStart.value)
          .onTrue(Commands.runOnce(() -> elevatorSubsystem.stop()));
    } else {
      new JoystickButton(ps4Controller, PS4Controller.Button.kCross.value)
          .onTrue(new ElevatorTestCommand(elevatorSubsystem, 1));
      new JoystickButton(ps4Controller, PS4Controller.Button.kCircle.value)
          .onTrue(new ElevatorTestCommand(elevatorSubsystem, 2));
      new JoystickButton(ps4Controller, PS4Controller.Button.kTriangle.value)
          .onTrue(new ElevatorTestCommand(elevatorSubsystem, 3));
      // Use L1 button for level 0 (more reliable than POV button)
      new JoystickButton(ps4Controller, PS4Controller.Button.kL1.value)
          .onTrue(new ElevatorTestCommand(elevatorSubsystem, 0));
      
      // Add binding for elevator calibration (Share button)
      new JoystickButton(ps4Controller, PS4Controller.Button.kShare.value)
          .onTrue(new CalibrateElevatorCommand(elevatorSubsystem));

      new JoystickButton(ps4Controller, PS4Controller.Button.kSquare.value)
          .whileTrue(new LimelightDebugCommand(limelightSubsystem));

      // Shooter control - Cross button for prepare
      new JoystickButton(ps4Controller, PS4Controller.Button.kCross.value)
          .onTrue(Commands.either(
              new FineTuneShooterIntakeCommand(shooterSubsystem),
              new PrepareShooterCommand(shooterSubsystem),
              () -> shooterSubsystem.getState() == ShooterState.SHOOT_CORAL
          ));

      // Shooter control - R1 button
      new JoystickButton(ps4Controller, PS4Controller.Button.kR1.value)
          .onTrue(new ShootCommand(shooterSubsystem, elevatorSubsystem));
          
      // Emergency stop for elevator (Options button)
      new JoystickButton(ps4Controller, PS4Controller.Button.kOptions.value)
          .onTrue(Commands.runOnce(() -> elevatorSubsystem.stop()));
    }

    // command initializes itself once at the start, but doesn't update the starting pose?
    new JoystickButton(xboxController, XboxController.Button.kRightBumper.value)
        .whileTrue(
            new DefaultDriveCommand(
                driveSubsystem,
                () -> getForwardInput() * 0.25,
                () -> getStrafeInput() * 0.25,
                () -> getRotationInput() * 0.25
            )
        );

    new JoystickButton(ps4Controller, PS4Controller.Button.kR1.value)
        .whileTrue(
            new DefaultDriveCommand(
                driveSubsystem,
                () -> getForwardInput() * 0.25,
                () -> getStrafeInput() * 0.25,
                () -> getRotationInput() * 0.25
            )
        );
  }

  private void configureDefaultCommands() {
    driveSubsystem.setDefaultCommand(
        new DefaultDriveCommand(
            driveSubsystem,
            () -> getForwardInput() * 0.5,  // Forward/backward
            () -> getStrafeInput() * 0.5,   // Left/right
            () -> getRotationInput() * 0.5  // Rotation
        )
    );
    
    // Set Limelight debug command as default
    limelightSubsystem.setDefaultCommand(new LimelightDebugCommand(limelightSubsystem));
  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    // Create and return the autonomous command
    return autoChooser.getSelected();
  }
}