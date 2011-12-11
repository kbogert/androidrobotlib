package edu.uga.robots.NXT;

public class NXTMessages {
	
	// LCP DIRECT COMMANDS
	// Taken from Appendix 2 Lego-Mindstorms NXT Direct commands;
	
	public static final byte DIRECT_COMMAND_RESPONSE = 0x00;
	public static final byte SYSTEM_COMMAND_RESPONSE = 0x01;
	public static final byte REPLY_TELEGRAM = 0x02;
	public static final byte DIRECT_COMMAND_NO_RESPONSE = (byte)0x80;
	public static final byte SYSTEM_COMMAND_NO_RESPONSE = (byte)0x81;
	
	public static final byte STARTPROGRAM = 0x00;
	public static final byte STOPPROGRAM = 0x01;
	public static final byte PLAYSOUNDFILE = 0x02;
	public static final byte PLAYTONE = 0x03;
	public static final byte SETOUTPUTSTATE = 0x04;
		public static final byte MODE_MOTORON = 0x01;
		public static final byte MODE_BRAKE = 0x02;
		public static final byte MODE_REGULATED = 0x04;
		public static final byte REGULATION_MODE_IDLE = 0x00;
		public static final byte REGULATION_MODE_MOTOR_SPEED = 0x01;
		public static final byte REGULATION_MODE_MOTOR_SYNC = 0x02;
		public static final byte MOTOR_RUN_STATE_IDLE = 0x00;
		public static final byte MOTOR_RUN_STATE_RAMPUP = 0x10;
		public static final byte MOTOR_RUN_STATE_RUNNING = 0x20;
		public static final byte MOTOR_RUN_STATE_RAMPDOWN = 0x40;
	public static final byte SETINPUTSTATE = 0x05;
		public static final byte NO_SENSOR = 0x00;
		public static final byte SWITCH = 0x01;
		public static final byte TEMPERATURE = 0x02;
		public static final byte REFLECTION = 0x03;
		public static final byte ANGLE = 0x04;
		public static final byte LIGHT_ACTIVE = 0x05;
		public static final byte LIGHT_INACTIVE = 0x06;
		public static final byte SOUND_DB = 0x07;
		public static final byte SOUND_DBA = 0x08;
		public static final byte CUSTOM = 0x09;
		public static final byte LOWSPEED = 0x0A;
		public static final byte LOWSPEED_9V = 0x0B;
		public static final byte NO_OF_SENSOR_TYPES = 0x0C;
		public static final byte RAWMODE = 0x00;
		public static final byte BOOLEANMODE = 0x20;
		public static final byte TRANSITIONCNTMODE = 0x40;
		public static final byte PERIODCOUNTERMODE = 0x60;
		public static final byte PCTFULLSCALEMODE = (byte)0x80;
		public static final byte CELSIUSMODE = (byte)0xA0;
		public static final byte FAHRENHEITMODE = (byte)0xC0;
		public static final byte ANGLESTEPSMODE = (byte)0xE0;
		public static final byte SLOPEMASK = 0x1F;
		public static final byte MODEMASK = (byte)0xE0;
	public static final byte GETOUTPUTSTATE = 0x06;
	public static final byte GETINPUTVALUES = 0x07;
	public static final byte RESETINPUTSCALEDVALUE = 0x08;
	public static final byte MESSAGEWRITE = 0x09;
	public static final byte RESETMOTORPOSITION = 0x0A;
	public static final byte GETBATTERYLEVEL = 0x0B;
	public static final byte STOPSOUNDPLAYBACK = 0x0C;
	public static final byte KEEPALIVE = 0x0D;
	public static final byte LSGETSTATUS = 0x0E;
	public static final byte LSWRITE = 0x0F;
	public static final byte LSREAD = 0x10;
	public static final byte GETCURRENTPROGRAMNAME = 0x11;
	public static final byte MESSAGEREAD = 0x13;



    public static byte[] getMotorMessage(int motor, int speed) {
        byte[] message = new byte[12];

        message[0] = DIRECT_COMMAND_NO_RESPONSE;
        message[1] = SETOUTPUTSTATE;
        // Output port
        message[2] = (byte) motor;

        if (speed == 0) {
            message[3] = 0;
            message[4] = 0;
            message[5] = 0;
            message[6] = 0;
            message[7] = 0;

        } else {
            // Power set option (Range: -100 - 100)
            message[3] = (byte) speed;
            // Mode byte
            message[4] = MODE_MOTORON & MODE_REGULATED;
            // Regulation mode
            message[5] = REGULATION_MODE_MOTOR_SPEED;
            // Turn Ratio (SBYTE; -100 - 100)
            message[6] = 0x00;
            // RunState
            message[7] = MOTOR_RUN_STATE_RUNNING;
        }

        // TachoLimit: run forever
        message[8] = 0;
        message[9] = 0;
        message[10] = 0;
        message[11] = 0;

        return message;

    }


    public static byte[] getMotorMessageSync(int motor, int speed, int turnRatio) {
        byte[] message = new byte[12];

        message[0] = DIRECT_COMMAND_NO_RESPONSE;
        message[1] = SETOUTPUTSTATE;
        // Output port
        message[2] = (byte) motor;

        if (speed == 0) {
            message[3] = 0;
            message[4] = 0;
            message[5] = 0;
            message[6] = 0;
            message[7] = 0;

        } else {
            // Power set option (Range: -100 - 100)
            message[3] = (byte) speed;
            // Mode byte
            message[4] = MODE_MOTORON & MODE_REGULATED;
            // Regulation mode
            message[5] = REGULATION_MODE_MOTOR_SYNC;
            // Turn Ratio (SBYTE; -100 - 100)
            message[6] = (byte)turnRatio;
            // RunState
            message[7] = MOTOR_RUN_STATE_RUNNING;
        }

        // TachoLimit: run forever
        message[8] = 0;
        message[9] = 0;
        message[10] = 0;
        message[11] = 0;

        return message;

    }
    
    public static byte[] getMotorMessage(int motor, int speed, int end) {
        byte[] message = getMotorMessage(motor, speed);

        // TachoLimit
        message[8] = (byte) end;
        message[9] = (byte) (end >>> 8);
        message[10] = (byte) (end >>> 16);
        message[11] = (byte) (end >>> 24);

        return message;
    }
    
    public static byte[] getMotorMessageSync(int motor, int speed, int end, int turnRatio) {
        byte[] message = getMotorMessageSync(motor, speed, turnRatio);
        
        // TachoLimit
        message[8] = (byte) end;
        message[9] = (byte) (end >>> 8);
        message[10] = (byte) (end >>> 16);
        message[11] = (byte) (end >>> 24);

        return message;
    }
    
    public static byte[] getMotorIdleMessage(int motor) {
        byte[] message = new byte[12];

        message[0] = DIRECT_COMMAND_NO_RESPONSE;
        message[1] = SETOUTPUTSTATE;
        // Output port
        message[2] = (byte) motor;

            // Power set option (Range: -100 - 100)
            message[3] = (byte) 0;
            // Mode byte
            message[4] = 0x00;
            // Regulation mode
            message[5] = REGULATION_MODE_IDLE;
            // Turn Ratio (SBYTE; -100 - 100)
            message[6] = 0x00;
            // RunState: MOTOR_RUN_STATE_RUNNING
            message[7] = MOTOR_RUN_STATE_IDLE;

        // TachoLimit: run forever
        message[8] = 0;
        message[9] = 0;
        message[10] = 0;
        message[11] = 0;

        return message;

    }
    
    public static byte[] getResetMotorPositionMessage(int motor) {
        byte[] message = new byte[4];

        message[0] = DIRECT_COMMAND_NO_RESPONSE;
        message[1] = RESETMOTORPOSITION;
        // Output port
        message[2] = (byte) motor;
        // absolute position
        message[3] = 0;

        return message;
    }
	
	
    
    public static byte[] getResetInputScaledValueMessage(int port) {
        byte[] message = new byte[3];

        message[0] = DIRECT_COMMAND_NO_RESPONSE;
        message[1] = RESETINPUTSCALEDVALUE;
        message[2] = (byte) port;

        return message;
    }
    
    
    public static byte[] getGetInputValuesMessage(int port) {
        byte[] message = new byte[3];

        message[0] = DIRECT_COMMAND_RESPONSE;
        message[1] = GETINPUTVALUES;
        message[2] = (byte) port;

        return message;
    }
    

    public static byte[] getSetInputStateMessage(int port, int sensor_type, int sensor_mode) {
        byte[] message = new byte[5];

        message[0] = DIRECT_COMMAND_NO_RESPONSE;
        message[1] = SETINPUTSTATE;
        // Output port
        message[2] = (byte) port;
        message[3] = (byte) sensor_type;
        message[4] = (byte) sensor_mode;

        return message;
    }

    public static byte[] getLSWriteMessage(int port, byte[] txData, int rx_size) {
        byte[] message = new byte[5 + txData.length];

        message[0] = DIRECT_COMMAND_NO_RESPONSE;
        message[1] = LSWRITE;
        // Output port
        message[2] = (byte) port;
        message[3] = (byte) txData.length;
        message[4] = (byte) rx_size;
        
        for (int i = 0; i < txData.length; i ++) {
        	message[5+i] = txData[i];
        }

        return message;
    }
    
    public static byte[] getLSReadMessage(int port) {
        byte[] message = new byte[3];

        message[0] = DIRECT_COMMAND_RESPONSE;
        message[1] = LSREAD;
        message[2] = (byte)port;

        return message;
    }
    

    public static byte[] getGetOutputStateMessage(int motor) {
        byte[] message = new byte[3];

        message[0] = DIRECT_COMMAND_RESPONSE;
        message[1] = GETOUTPUTSTATE;
        message[2] = (byte) motor;

        return message;
    }
}
