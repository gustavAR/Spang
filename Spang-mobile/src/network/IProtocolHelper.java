package network;

public interface IProtocolHelper {
	byte[] processRecivedMessage(byte[] message);
	byte[] processSentMessage(byte[] message);
	Protocol getProtocol();
}