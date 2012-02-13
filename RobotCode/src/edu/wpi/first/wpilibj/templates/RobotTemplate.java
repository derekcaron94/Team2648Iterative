/*----------------------------------------------------------------------------*/
/* Welcome to FRC Programming!                                                */
/*                                                                            */
/* Please take a look at the ReadMe.txt file in this source before diving     */
/* into the code.  It'll explain how it's all setup in the beginning.         */
/*----------------------------------------------------------------------------*/

/*
 * Please remember to look through the other files in this package, as well.
 * All of the camera operations are contained in Camera2012.java, which does
 * "extend" the Robot2012 class.  That means that it can add onto the functions
 * here.  For example, while this file tells the robot how to drive during
 * teleoperation, the Camera2012 class also gives it instructions on dealing
 * with camera commands during teleoperation.
 * 
 * Settings.java is an "interface"... it doesn't give commands like this file,
 * it only holds variables which this class can use.  For example, there is a
 * variable in Settings.java called "debugTesting".  If it is 1, things may
 * act differently here and in Camera2012.java.  You'll notice these features
 * by the line "if(debugTesting == 1) {".
 */

/*
 * This set of code is packaged together under the title "frc2012".  This
 * particular class (Robot2012) requires that we import several things.
 * 
 * import edu.wpi.first.wpilibj.*; imports almost everything in the library,
 * except for its subfolders (like .camera).  This includes all of the library
 * files necessary for using a joystick, driverStation, etc.
 */
package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.*;
import java.io.IOException;
import java.io.InputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.SocketConnection;

public class RobotTemplate extends IterativeRobot {

    Jaguar lt1 = new Jaguar(1);
    Jaguar rt1 = new Jaguar(2);
    RobotDrive driveTrain = new RobotDrive(lt1, rt1);
    Victor intake1 = new Victor(5);
    Victor intake2 = new Victor(6);
    Victor shoot1 = new Victor(3);
    Victor shoot2 = new Victor(4);
    Relay robotDownerator = new Relay(1);
    Relay rampDown = new Relay(2);
    /*
     * The driver's station is broken up into two parts - the station itself,
     * which connects to the joysticks, and the LCD which can be used to output
     * data. Calling "getInstance()" connects the robot and the driver's station
     * together.
     */
    DriverStation driverStation = DriverStation.getInstance();
    DriverStationLCD dsLCD = DriverStationLCD.getInstance();
    Joystick joyLeft = new Joystick(1);
    Joystick joyRight = new Joystick(2);
    Timer time = new Timer();
    public int operationState = 0;
    int teleopPacketCount = 0;
    double encoderLeftValue = 0;
    double encoderRightValue = 0;
    double gyroValue = 0;
    double accelValue = 0;
    private double value1 = 0, value2 = 0;

    public void robotInit() {
        printLCD(1, "Initializing...     ");
        resetRobot(1, 1, 1);
    }

    public void disabledInit() {
    }

    public void disabledPeriodic() {
    }

    public void disabledContinuous() {
    }

    public void autonomousInit() {
        printLCD(1, "Autonomous          ");
        resetRobot(1, 1, 1);
    }

    public void autonomousPeriodic() {
    }

    public void autonomousContinuous() {
    }

    public void teleopInit() {
        printLCD(1, "Teleoperation       ");
        resetRobot(1, 1, 1);
    }

    public void teleopPeriodic() {

        //Driverstation User Messages
//        printLCD(3, "Encoder left " + encoderLeftValue + " Encoder right " + encoderRightValue + " Gyro Value " + (int) (gyroValue));
        if (driverStation.getBatteryVoltage() <= 8) {
            printLCD(6, "WARNING BATTERY LOW");
        }
        printLCD(4, "TOP SHOOTER: " + driverStation.getAnalogIn(1) + " BOTTEM SHOOTER: " + driverStation.getAnalogIn(2));
        driveTrain.arcadeDrive(joyRight);
        driveTrain.setSafetyEnabled(false);

        //Operator Controls
        if (teleopPacketCount % 4 == 0) {
            //encoderLeftValue = encoderLeft.getRate();
        } else if (teleopPacketCount % 4 == 1) {
            //encoderRightValue = encoderRight.getRate();
        } else if (teleopPacketCount % 4 == 2) {
            //gyroValue = gyro.getAngle();
        } else if (teleopPacketCount % 4 == 3) {
            value1 = -(driverStation.getAnalogIn(1));
            value2 = -(driverStation.getAnalogIn(2));
        }

        //Lifter Code
        if (joyLeft.getRawButton(1) || joyRight.getRawButton(3)) {
            if (!joyRight.getRawButton(1) || !joyLeft.getRawButton(8)) {//If we arnt pressing the shooter buttons
                shoot1.set(.3);//keep le ball in
                shoot2.set(.3);
            }
            intake1.set(-1);//run lift up
            intake2.set(1);
        } else if (joyLeft.getRawButton(2) || joyRight.getRawButton(3)) {//run it down
            intake1.set(1);
            intake2.set(-1);
        } else {//dont run lifts
            intake1.set(0);
            intake2.set(0);
            if (!joyRight.getRawButton(1) || !joyLeft.getRawButton(8)) {//if we arnt pressing the shooter buttons stop em
                shoot1.set(0);
                shoot2.set(0);
            }
        }



        //Shooter
        if (joyRight.getRawButton(1) || joyLeft.getRawButton(8)) {//fire the shooter
            shoot1.set(value1);
            shoot2.set(value2);
        } else {//stop it
            shoot1.set(0);
            shoot2.set(0);
        }

        //Bridge Pusherdownarator
        if (joyLeft.getRawButton(5) || joyRight.getRawButton(6)) {//arm press down
            robotDownerator.set(Relay.Value.kForward);
        } else if (joyLeft.getRawButton(3) || joyRight.getRawButton(7)) {//arm up
            robotDownerator.set(Relay.Value.kReverse);
        } else {//off
            robotDownerator.set(Relay.Value.kOff);
        }

        //Ramp
        if (joyLeft.getRawButton(11)) {//ramp down
            rampDown.set(Relay.Value.kForward);
        } else if (joyRight.getRawButton(12)) {//only needs for reset
            rampDown.set(Relay.Value.kReverse);
        } else {//off
            rampDown.set(Relay.Value.kOff);
        }



        teleopPacketCount++;
    }

    public void teleopContinuous() {
    }

    public void printLCD(int line, String text) {
        if (line == 1) {
            dsLCD.println(DriverStationLCD.Line.kMain6, 1, text);
        } else if (line == 2) {
            dsLCD.println(DriverStationLCD.Line.kUser2, 1, text);
        } else if (line == 3) {
            dsLCD.println(DriverStationLCD.Line.kUser3, 1, text);
        } else if (line == 4) {
            dsLCD.println(DriverStationLCD.Line.kUser4, 1, text);
        } else if (line == 5) {
            dsLCD.println(DriverStationLCD.Line.kUser5, 1, text);
        } else if (line == 6) {
            dsLCD.println(DriverStationLCD.Line.kUser6, 1, text);
        }
        dsLCD.updateLCD();
    }

    public void resetRobot(int state, int packets, int sensors) {
        if (state == 1) {
            operationState = 0;
        }
        if (state == 2) {
            operationState = 1;
        }
        if (packets == 1) {
            teleopPacketCount = 0;
        }
        if (sensors == 1) {
//            encoderLeft.reset();
//            encoderRight.reset();
//            gyro.reset();
//
//            encoderLeftValue = 0;
//            encoderRightValue = 0;
//            gyroValue = 0;
//            accelValue = 0;
        }

//        if (connect == 1) {
//            if (socketConnect != null) {
//                try {
//                    socketConnect.close();
//                } catch (IOException ex) {
//                }
//            }
//            if (myInputStream != null) {
//                try {
//                    myInputStream.reset();
//                } catch (IOException ex) {
//                }
//            }
//        }
//        if (connect == 2) {
//            try {
//                socketConnect = (SocketConnection) Connector.open("socket://10.46.48.5:1180");
//                myInputStream = socketConnect.openInputStream();
//            } catch (Exception e) {
//            }
//        }

    }
}