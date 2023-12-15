package model;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

public class CapturePacket implements Serializable {

	// Start bytes
	private byte startByte1 = 0x50;
	private byte startByte2 = 0x54;

	// Length = total bytes - 6
	private byte length;
	private byte groupId;

	// Axis ID - 0x01 - Yaw, 0x02 - Pitch, 0x03 - Roll
	private byte axisId;

	private short opcode;
	private byte opcodeHigh;
	private byte opcodeLow;

	private byte[] dataArray;
	private byte checksum;

	public CapturePacket() {
	}

	public byte[] getPacketBytes() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		baos.write(startByte1);
		baos.write(startByte2);
		baos.write(length);
		baos.write(groupId);
		baos.write(axisId);
		baos.write(opcodeHigh);
		baos.write(opcodeLow);

		if (length != 0x04) {
			for (byte data : dataArray)
				baos.write(data);
		}

		baos.write(calculateChecksum());

		return baos.toByteArray();
	}

	public byte calculateChecksum() {

		int sum = 0;

		sum += (byte) (length & 0xFF);
		sum += (byte) (groupId & 0xFF);
		sum += (byte) (axisId & 0xFF);
		sum += (byte) (opcodeHigh & 0xFF);
		sum += (byte) (opcodeLow & 0xFF);
		
		if (length != 0x04) 
		for (byte data : dataArray) {
			sum += (data & 0xFF);
		}

		return (byte) (sum % 256);
	}

	public static CapturePacket deserialize(byte[] packetBytes) {
		CapturePacket packet = new CapturePacket();

		// Check if this is an ack/nak
		if(packetBytes.length == 1)

		// Validate start bytes
		if (packetBytes[0] != (byte) 0x50 || packetBytes[1] != (byte) 0x54) {
			throw new IllegalArgumentException("Invalid start bytes");
		}


		int pos = 2;

		// Extract length
		packet.length = packetBytes[pos++];

		// Extract group ID
		packet.groupId = packetBytes[pos++];

		// Extract axis ID
		packet.axisId = packetBytes[pos++];

		// Extract opcode
		packet.opcodeHigh = packetBytes[pos++];
		packet.opcodeLow = packetBytes[pos++];
		packet.opcode = (short) ((packet.opcodeHigh << 8) | packet.opcodeLow);

		// Extract data bytes to array
		if (packet.length != 0x04) {
			packet.dataArray = new byte[packet.length - 4];
			System.arraycopy(packetBytes, pos, packet.dataArray, 0, packet.dataArray.length);
			pos += packet.dataArray.length;
		}
		// Extract checksum
		packet.checksum = packetBytes[pos];

		return packet;
	}

	public byte getLength() {
		return length;
	}

	public byte getGroupId() {
		return groupId;
	}

	public byte getAxisId() {
		return axisId;
	}

	public short getOpcode() {
		return opcode;
	}

	public byte getOpcodeHigh() {
		return opcodeHigh;
	}

	public byte getOpcodeLow() {
		return opcodeLow;
	}

	public byte[] getDataArray() {
		return dataArray;
	}

	public byte getChecksum() {
		return checksum;
	}

	public void setLength(byte length) {
		this.length = length;
	}

	public void setGroupId(byte groupId) {
		this.groupId = groupId;
	}

	public void setAxisId(byte axisId) {
		this.axisId = axisId;
	}

	public void setOpcode(short opcode) {
		this.opcode = opcode;
	}

	public void setOpcodeHigh(byte opcodeHigh) {
		this.opcodeHigh = opcodeHigh;
	}

	public void setOpcodeLow(byte opcodeLow) {
		this.opcodeLow = opcodeLow;
	}

	public void setDataArray(byte[] dataArray) {
		this.dataArray = dataArray;
	}

	public void setChecksum(byte checksum) {
		this.checksum = checksum;
	}

}
