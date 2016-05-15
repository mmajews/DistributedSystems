package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jgroups.JChannel;
import org.jgroups.Receiver;
import org.jgroups.ReceiverAdapter;
import org.jgroups.protocols.BARRIER;
import org.jgroups.protocols.FD_ALL;
import org.jgroups.protocols.FD_SOCK;
import org.jgroups.protocols.FRAG2;
import org.jgroups.protocols.MERGE2;
import org.jgroups.protocols.MFC;
import org.jgroups.protocols.PING;
import org.jgroups.protocols.UDP;
import org.jgroups.protocols.UFC;
import org.jgroups.protocols.UNICAST2;
import org.jgroups.protocols.VERIFY_SUSPECT;
import org.jgroups.protocols.pbcast.FLUSH;
import org.jgroups.protocols.pbcast.GMS;
import org.jgroups.protocols.pbcast.NAKACK;
import org.jgroups.protocols.pbcast.STABLE;
import org.jgroups.protocols.pbcast.STATE_TRANSFER;
import org.jgroups.stack.ProtocolStack;
import pl.edu.agh.dsrg.sr.chat.protos.ChatOperationProtos;
import receivers.MessageReceiver;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class JChannelUtils
{
	private static final Logger logger = LogManager.getLogger(JChannelUtils.class);
	public static JChannel initializeChannel(String nick, String nameOfChannel, ReceiverAdapter receiver, String multicastAddress){
		logger.debug("Creating JChannel for name: {} and multicast address: {}", nick, nameOfChannel);
		JChannel jChannel = null;

		try{
			jChannel = createBasicChannel(multicastAddress);
			jChannel.setName(nick);
			jChannel.setReceiver(receiver);
			jChannel.connect(nameOfChannel);
		}
		catch (UnknownHostException unknownHostException){
			logger.error("Unknown host for address : {} ", multicastAddress,unknownHostException);
			System.exit(1);
		} catch (Exception e) {
			logger.error("Unexpected error occured why trying to create JChannel", e);
			System.exit(1);
		}
		return jChannel;
	}

	private static JChannel createBasicChannel(String multicastAddress) throws UnknownHostException{
		JChannel channel = new JChannel(false);
		ProtocolStack protocolStack = new ProtocolStack();
		protocolStack.addProtocol(new UDP().setValue("mcast_group_addr", InetAddress.getByName(multicastAddress)))
				.addProtocol(new PING())
				.addProtocol(new MERGE2())
				.addProtocol(new FD_SOCK())
				.addProtocol(new FD_ALL().setValue("timeout", 12000).setValue("interval", 3000))
				.addProtocol(new VERIFY_SUSPECT())
				.addProtocol(new BARRIER())
				.addProtocol(new NAKACK())
				.addProtocol(new UNICAST2())
				.addProtocol(new STABLE())
				.addProtocol(new GMS())
				.addProtocol(new UFC())
				.addProtocol(new MFC())
				.addProtocol(new FRAG2())
				.addProtocol(new STATE_TRANSFER())
				.addProtocol(new FLUSH());
		channel.setProtocolStack(protocolStack);

		try {
			protocolStack.init();
		} catch (Exception e) {
			logger.error("There was a problem why initializing protocol stack",e);
			System.exit(1);
		}
		return channel;
	}

}
