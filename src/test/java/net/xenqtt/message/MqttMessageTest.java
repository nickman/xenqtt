/**
    Copyright 2013 James McClure

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package net.xenqtt.message;

import static org.junit.Assert.*;

import java.nio.ByteBuffer;

import net.xenqtt.message.MessageType;
import net.xenqtt.message.MqttMessage;
import net.xenqtt.message.QoS;

import org.junit.Test;

public class MqttMessageTest {

	static final byte[] PAYLOAD = new byte[] { 27, 24, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

	@Test
	public void testBytesToHex_NullArray() throws Exception {

		assertNull(MqttMessage.bytesToHex(null));
	}

	@Test
	public void testBytesToHex_EmptyArray() throws Exception {

		assertEquals("", MqttMessage.bytesToHex(new byte[0]));
	}

	@Test
	public void testBytesToHex_ArrayWithData() throws Exception {

		assertEquals("01 02 7f", MqttMessage.bytesToHex(new byte[] { 1, 2, 127 }));
	}

	@Test
	public void testOutboundCtor_MessageTypeAndRemainingLength() {
		MqttMessage message = new MqttMessage(MessageType.CONNECT, 24);
		message.buffer.flip();

		assertEquals(MessageType.CONNECT, message.getMessageType());
		assertEquals(24, message.getRemainingLength());
		assertEquals(QoS.AT_MOST_ONCE, message.getQoS());
	}

	@Test
	public void testOutboundCtor_MessageTypeAndRemainingLengthAndFlags_FalseOnAllFlags() {
		MqttMessage message = new TestMessage(MessageType.CONNECT, false, QoS.AT_LEAST_ONCE, false, 24);
		message.buffer.flip();

		assertEquals(MessageType.CONNECT, message.getMessageType());
		assertEquals(24, message.getRemainingLength());
		assertEquals(QoS.AT_LEAST_ONCE, message.getQoS());
		assertEquals(0, message.buffer.get(0) & 0x08);
		assertEquals(0, message.buffer.get(0) & 0x01);
	}

	@Test
	public void testOutboundCtor_MessageTypeAndRemainingLengthAndFlags_Duplicate() {
		MqttMessage message = new MqttMessage(MessageType.CONNECT, true, QoS.AT_LEAST_ONCE, false, 24);
		message.buffer.flip();

		assertEquals(MessageType.CONNECT, message.getMessageType());
		assertEquals(24, message.getRemainingLength());
		assertEquals(QoS.AT_LEAST_ONCE, message.getQoS());
		assertEquals(0x08, message.buffer.get(0) & 0x08);
		assertEquals(0, message.buffer.get(0) & 0x01);
	}

	@Test
	public void testOutboundCtor_MessageTypeAndRemainingLengthAndFlags_Retain() {
		MqttMessage message = new MqttMessage(MessageType.CONNECT, false, QoS.AT_LEAST_ONCE, true, 24);
		message.buffer.flip();

		assertEquals(MessageType.CONNECT, message.getMessageType());
		assertEquals(24, message.getRemainingLength());
		assertEquals(QoS.AT_LEAST_ONCE, message.getQoS());
		assertEquals(0, message.buffer.get(0) & 0x08);
		assertEquals(0x01, message.buffer.get(0) & 0x01);
	}

	@Test
	public void testOutboundCtor_MessageTypeAndRemainingLengthAndFlags_AllFlags() {
		MqttMessage message = new MqttMessage(MessageType.CONNECT, true, QoS.AT_LEAST_ONCE, true, 24);
		message.buffer.flip();

		assertEquals(MessageType.CONNECT, message.getMessageType());
		assertEquals(24, message.getRemainingLength());
		assertEquals(QoS.AT_LEAST_ONCE, message.getQoS());
		assertEquals(0x08, message.buffer.get(0) & 0x08);
		assertEquals(0x01, message.buffer.get(0) & 0x01);
	}

	@Test
	public void testInboundCtor() {
		MqttMessage message = new MqttMessage(ByteBuffer.wrap(PAYLOAD), 24, 123);

		assertEquals(MessageType.CONNECT, message.getMessageType());
		assertEquals(24, message.getRemainingLength());
		assertEquals(QoS.AT_LEAST_ONCE, message.getQoS());
		assertEquals(0x08, message.buffer.get(0) & 0x08);
		assertEquals(0x01, message.buffer.get(0) & 0x01);
		assertEquals(123, message.getReceivedTimestamp());

		assertArrayEquals(PAYLOAD, message.buffer.array());
	}

	@Test
	public void testIsAckable() throws Exception {

		// QOS 0 is not ackable
		assertFalse(new TestMessage(MessageType.PUBLISH, 0).isAckable());

		// QOS 1 is ackable
		TestMessage msg = new TestMessage(MessageType.PUBLISH, 0);
		msg.buffer.put(0, (byte) (msg.buffer.get(0) | 0x02));
		assertTrue(msg.isAckable());
	}

	@Test
	public void testIsAck() throws Exception {
		assertFalse(new TestMessage(MessageType.CONNACK, 0).isAck());
		assertFalse(new TestMessage(MessageType.CONNECT, 0).isAck());
		assertFalse(new TestMessage(MessageType.DISCONNECT, 0).isAck());
		assertFalse(new TestMessage(MessageType.PINGREQ, 0).isAck());
		assertFalse(new TestMessage(MessageType.PINGRESP, 0).isAck());
		assertTrue(new TestMessage(MessageType.PUBACK, 0).isAck());
		assertTrue(new TestMessage(MessageType.PUBCOMP, 0).isAck());
		assertFalse(new TestMessage(MessageType.PUBLISH, 0).isAck());
		assertTrue(new TestMessage(MessageType.PUBREC, 0).isAck());
		assertFalse(new TestMessage(MessageType.PUBREL, 0).isAck());
		assertTrue(new TestMessage(MessageType.SUBACK, 0).isAck());
		assertFalse(new TestMessage(MessageType.SUBSCRIBE, 0).isAck());
		assertTrue(new TestMessage(MessageType.UNSUBACK, 0).isAck());
		assertFalse(new TestMessage(MessageType.UNSUBSCRIBE, 0).isAck());
	}

	@Test
	public void testSetDuplicateFlag() throws Exception {

		MqttMessage message = new TestMessage(MessageType.CONNECT, false, QoS.AT_LEAST_ONCE, false, 24);
		message.buffer.flip();

		assertEquals(MessageType.CONNECT, message.getMessageType());
		assertEquals(24, message.getRemainingLength());
		assertEquals(QoS.AT_LEAST_ONCE, message.getQoS());
		assertEquals(0, message.buffer.get(0) & 0x08);
		assertEquals(0, message.buffer.get(0) & 0x01);
		assertFalse(message.isDuplicate());
		assertFalse(message.isRetain());

		message.setDuplicateFlag();

		assertEquals(MessageType.CONNECT, message.getMessageType());
		assertEquals(24, message.getRemainingLength());
		assertEquals(QoS.AT_LEAST_ONCE, message.getQoS());
		assertEquals(0x08, message.buffer.get(0) & 0x08);
		assertEquals(0, message.buffer.get(0) & 0x01);
		assertTrue(message.isDuplicate());
		assertFalse(message.isRetain());
	}

	private static class TestMessage extends MqttMessage {

		public TestMessage(ByteBuffer buffer, int remainingLength) {
			super(buffer, remainingLength, 0);
		}

		public TestMessage(MessageType messageType, boolean duplicate, QoS qos, boolean retain, int remainingLength) {
			super(messageType, duplicate, qos, retain, remainingLength);
		}

		public TestMessage(MessageType messageType, int remainingLength) {
			super(messageType, remainingLength);
		}
	}
}
