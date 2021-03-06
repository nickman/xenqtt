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
package net.xenqtt.client;

import static org.junit.Assert.*;
import net.xenqtt.client.FixedReconnectionStrategy;
import net.xenqtt.client.ReconnectionStrategy;

import org.junit.Test;

public class FixedReconnectionStrategyTest {

	FixedReconnectionStrategy strategy = new FixedReconnectionStrategy(7000, 3);

	@Test(expected = IllegalArgumentException.class)
	public void testCtor_ReconnectMillisZero() {
		new FixedReconnectionStrategy(0, 1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCtor_ReconnectAttemptsLessThanZero() {
		new FixedReconnectionStrategy(7000, -1);
	}

	@Test
	public void testConnectionLost() {
		assertEquals(7000, strategy.connectionLost(null, null));
		assertEquals(7000, strategy.connectionLost(null, null));
		assertEquals(7000, strategy.connectionLost(null, null));
		assertEquals(-1, strategy.connectionLost(null, null));
		assertEquals(-1, strategy.connectionLost(null, null));
	}

	@Test
	public void testConnectionEstablished() {
		assertEquals(7000, strategy.connectionLost(null, null));
		assertEquals(7000, strategy.connectionLost(null, null));
		assertEquals(7000, strategy.connectionLost(null, null));
		assertEquals(-1, strategy.connectionLost(null, null));
		assertEquals(-1, strategy.connectionLost(null, null));

		strategy.connectionEstablished();
		assertEquals(7000, strategy.connectionLost(null, null));
		assertEquals(7000, strategy.connectionLost(null, null));
		assertEquals(7000, strategy.connectionLost(null, null));
		assertEquals(-1, strategy.connectionLost(null, null));
		assertEquals(-1, strategy.connectionLost(null, null));
	}

	@Test
	public void testClone() throws Exception {

		assertEquals(7000, strategy.connectionLost(null, null));
		assertEquals(7000, strategy.connectionLost(null, null));
		assertEquals(7000, strategy.connectionLost(null, null));
		assertEquals(-1, strategy.connectionLost(null, null));
		assertEquals(-1, strategy.connectionLost(null, null));

		ReconnectionStrategy clone = strategy.clone();
		assertNotSame(strategy, clone);

		assertEquals(7000, clone.connectionLost(null, null));
		assertEquals(7000, clone.connectionLost(null, null));
		assertEquals(7000, clone.connectionLost(null, null));
		assertEquals(-1, clone.connectionLost(null, null));
		assertEquals(-1, clone.connectionLost(null, null));
	}
}
