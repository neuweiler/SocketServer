package biz.neuweiler.socketserver;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Locale;

import net.iharder.Base64;

/**
 * This program is intended to serve as a websocket server during GEVCU website
 * development. It provides more or less meaningfull values as a running GEVCU
 * would so you can test and debug the website.
 * 
 * @author Michael Neuweiler
 *
 */

public class SocketServer {

	public static void main(String[] args) throws Exception {
		ServerSocket serverSocket = null;
		boolean listening = true;

		try {
			System.out.println("opening socket port 2000");
			serverSocket = new ServerSocket(2000);
		} catch (IOException e) {
			System.err.println("Could not listen on port: 2000.");
			System.exit(-1);
		}

		while (listening) {
			System.out.println("listening...");
			new ServerThread(serverSocket.accept()).start();
		}
		System.out.println("shutting down");
		serverSocket.close();
	}
}

class ServerThread extends Thread {
	private Socket socket = null;

	public ServerThread(Socket socket) {
		super("ServerThread");
		this.socket = socket;
	}

	public void run() {
		byte buf[] = new byte[80];
		System.out.println("run");
		try {
			OutputStream outStream = null;
			PrintWriter out = new PrintWriter(outStream = socket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			System.out.println("processing headers");
			// Handle the headers first
			doHeaders(out, in);
			InputStream is = socket.getInputStream();
			System.out.println("headers processed, handling incoming data...:");

			Thread.sleep(2000); // delay a bit to simulate slow ichip

			Locale.setDefault(Locale.US);
			
			int i = 0;
			while (socket != null) {
				String dashboard = new String("{\r"
						+ String.format("\"timeRunning\": \"%s\"", new Date().toString().substring(11, 19))
//						+ String.format(",\"systemState\": %d", (i < 90 ? (i / 10 + 1) : 99)) +
						+ String.format(",\"systemState\": %d", (i < 90 ? 8 : 5))
						+ String.format(",\"enableRegen\": %s", ((i / 10) % 2 == 0 ? "true" : "false"))
						+ String.format(",\"enableHeater\": %s", ((i / 20) % 2 == 0 ? "true" : "false"))
						+ String.format(",\"enableCreep\": %s", ((i / 5) % 2 == 0 ? "true" : "false"))
						+ String.format(",\"powerSteering\": %s", (((i + 3) / 10) % 2 == 0 ? "true" : "false"))
						+ String.format(",\"throttle\": %.0f", (-100 + Math.random() * 200))
//						+ String.format(",\"brake\": 0.0,") +
//						+ String.format(",\"gear\": 0,") +
//						+ String.format(",\"torqueRequested\": %d", i) +
						+ String.format(",\"torqueActual\": %.0f", (-220 + Math.random() * 440))
						+ String.format(",\"speedActual\": %d", i * 90)
						+ String.format(",\"dcVoltage\": %.4f", (i * 2.2f + 220))
						+ String.format(",\"dcCurrent\": %f", (i * 5.5f - 275))
//						+ String.format(",\"mechanicalPower\": %d", (i * 2.5f - 125))
						+ String.format(",\"temperatureMotor\": %f", i * 1.5f)
						+ String.format(",\"temperatureController\": %d", i)
						
						+ String.format(",\"dcDcHvVoltage\": %.1f", i * 2.2f + 221)
						+ String.format(",\"dcDcHvCurrent\": %.1f", i / 25.0f)
						+ String.format(",\"dcDcLvVoltage\": %.0f", i / 25.0f + 10)
						+ String.format(",\"dcDcLvCurrent\": %d", (i * 2))
						+ String.format(",\"dcDcTemperature\": %.1f", i / 2.0f + 20)
						
						+ String.format(",\"chargeLevel\": %d", i)
						+ String.format(",\"chargeHoursRemain\": %d", 10 - i / 10)
						+ String.format(",\"chargeMinsRemain\": %.0f", 59 - i * 0.6f)
						+ String.format(",\"chargerInputVoltage\": %.1f", i * 0.8f + 180)
						+ String.format(",\"chargerInputCurrent\": %.1f", i * 0.4)
						+ String.format(",\"chargerBatteryVoltage\": %.1f", i * 2.3f + 240)
						+ String.format(",\"chargerBatteryCurrent\": %.1f", i * 0.2f)
						+ String.format(",\"chargerTemperature\": %.1f", i / 2.0f + 20)
						
						+ String.format(",\"temperatureCoolant\": %d", i)
						+ String.format(",\"temperatureHeater\": %d", i)
						+ String.format(",\"heaterPower\": %d", i * 60)
						+ String.format(",\"flowCoolant\": %.2f", i / 25.0f + 10)
						+ String.format(",\"flowHeater\": %.2f", i / 25.0f + 10)
						+ String.format(",\"temperatureBattery1\": %d", i)
						+ String.format(",\"temperatureBattery2\": %d", i + 1)
						+ String.format(",\"temperatureBattery3\": %d", i + 2)
						+ String.format(",\"temperatureBattery4\": %d", i + 3)
						+ String.format(",\"temperatureBattery5\": %d", i + 4)
						+ String.format(",\"temperatureBattery6\": %d", i + 5)
						+ String.format(",\"temperatureExterior\": %d", i + 10)
						
						+ String.format(",\"packResistance\": %.2f", i / 5.0f)
						+ String.format(",\"packHealth\": %d", i)
						+ String.format(",\"packCycles\": %d", 1000 + i)
						+ String.format(",\"soc\": %d", 100 - i)
						+ String.format(",\"dischargeLimit\": %d", 240 - i)
						+ String.format(",\"chargeLimit\": %d", 180 - i)
						+ String.format(",\"chargeAllowed\": %s", ((i / 10) % 2 == 0 ? "true" : "false"))
						+ String.format(",\"dischargeAllowed\": %s", ((i / 10) % 2 == 1 ? "true" : "false"))
						+ String.format(",\"lowestCellTemp\": %d", i)
						+ String.format(",\"highestCellTemp\": %d", i)
						+ String.format(",\"lowestCellVolts\": %.4f", i / 30.0f)
						+ String.format(",\"highestCellVolts\": %.4f", (100 - i) / 30.0f)
						+ String.format(",\"averageCellVolts\": %d", i)
						+ String.format(",\"deltaCellVolts\": %.4f", i / 99f)
						+ String.format(",\"lowestCellResistance\": %.2f", i / 83f)
						+ String.format(",\"highestCellResistance\": %.2f", i / 85f)
						+ String.format(",\"averageCellResistance\": %.2f", i / 80f)
						+ String.format(",\"deltaCellResistance\": %.2f", i / 800f)
						+ String.format(",\"lowestCellTempId\": %d", i+75)
						+ String.format(",\"highestCellTempId\": %d", i+100)
						+ String.format(",\"lowestCellVoltsId\": %d", i+80)
						+ String.format(",\"highestCellVoltsId\": %d", i+120)
						+ String.format(",\"lowestCellResistanceId\": %d", i+90)
						+ String.format(",\"highestCellResistanceId\": %d", i+150)
						+ String.format(",\"bmsTemp\": %d", i)
						+ String.format(",\"cruiseSpeed\": %d", i % 10)

						+ ",\"limits\": { \"dcCurrent\": { \"min\": " + (-260 + i) + ",\"max\": " + (260 - i) + "}"
						+ ",\"dcVoltage\": { \"min\": " + (270 + i) + ",\"max\": " + (450 - i) + "}"
						+ ",\"temperatureMotor\": { \"max\": " + (150 - i) + "}"
						+ ",\"temperatureController\": { \"max\": " + (100 - i) + "}}");
				
				if (i < 30 && i > 10) {
					dashboard += String.format(",\"bitfieldMotor\": %.0f", (i + Math.random()) * 0x28F5C28)
					+ String.format(",\"bitfieldBms\": %.0f", (i + Math.random()) * 0x28F5C28)
					+ String.format(",\"bitfieldIO\": %.0f", (i + Math.random()) * 0x28F5C28);
				} else if (i == 40) {
					dashboard += ",\"bitfieldIO\": 0";
				}
				else if (i == 50) {
					dashboard += ",\"bitfieldMotor\": 0";
				}
				else if (i == 60) {
					dashboard += ",\"bitfieldBms\": 0";
				}
				dashboard += "}";
				sendData(outStream, dashboard);

				if (i++ > 99) {
					i = 0;
					// break;
				}

				if (i%30 == 0) {
					sendData(outStream, "{\r\"logMessage\": {\r\"level\": \"INFO\",\r\"message\": \"this is a notification message\"\r}\r}");
				}
				if (i == 60) {
					sendData(outStream, "{\r\"logMessage\": {\r\"level\": \"WARNING\",\r\"message\": \"warning: we're about to ...\"\r}\r}");
				}
				if (i == 90) {
//					sendData(outStream, "{\r\"logMessage\": {\r\"level\": \"ERROR\",\r\"message\": \"error: your car no longer supports oil wars! ;)\"\r}\r}");
				}

				if (is.available() != 0) {
					int len = is.read(buf);
					if (len > 0) {
						System.out.println("read bytes: " + len);
						processData(buf);
					}
				}
				Thread.sleep(1000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("terminating connection");
	}

	/*
	 * 
	 * 0 1 2 3 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
	 * +-+-+-+-+-------+-+-------------+-------------------------------+ |F|R|R|R|
	 * opcode|M| Payload len | Extended payload length | |I|S|S|S| (4) |A| (7) |
	 * (16/64) | |N|V|V|V| |S| | (if payload len==126/127) | | |1|2|3| |K| | |
	 * +-+-+-+-+-------+-+-------------+ - - - - - - - - - - - - - - - + | Extended
	 * payload length continued, if payload len == 127 | + - - - - - - - - - - - - -
	 * - - +-------------------------------+ | |Masking-key, if MASK set to 1 |
	 * +-------------------------------+-------------------------------+ |
	 * Masking-key (continued) | Payload Data | +-------------------------------- -
	 * - - - - - - - - - - - - - - + : Payload Data continued ... : + - - - - - - -
	 * - - - - - - - - - - - - - - - - - - - - - - - - + | Payload Data continued
	 * ... | +---------------------------------------------------------------+
	 */

	private void processData(byte[] buffer) {
		boolean fin = (buffer[0] & 0x80) != 0;
		int opcode = buffer[0] & 0x0f;
		boolean mask = (buffer[1] & 0x80) != 0;
		long payloadLength = buffer[1] & 0x7f;

		System.out.println("fin: " + fin + ", opcode: " + opcode + ", mask: " + mask + ", length: " + payloadLength);
		System.out.println("buffer: " + (buffer[0] & 0xff) + " " + (buffer[1] & 0xff) + " " + (buffer[2] & 0xff) + " "
				+ (buffer[3] & 0xff) + " " + (buffer[4] & 0xff) + " " + (buffer[5] & 0xff) + " " + (buffer[7] & 0xff)
				+ " " + (buffer[8] & 0xff));
		int offset = 2;
		if (payloadLength == 0x7e) { // 126 -> use next two bytes as unsigned 16bit length of payload
			payloadLength = buffer[offset] << 8 + buffer[offset + 1];
			System.out.println("extended 16-bit lenght: " + payloadLength);
			offset += 2;
		}
		if (payloadLength == 0x7f) { // 127 --> use following 8 bytes as unsigned 64bit length of payload
			payloadLength = buffer[offset] << 56 + buffer[offset + 1] << 48 + buffer[offset + 2] << 40
					+ buffer[offset + 3] << 32 + buffer[offset + 4] << 24
					& buffer[offset + 5] << 16 + buffer[offset + 6] << 8 + buffer[offset + 7];
			System.out.println("extended 64-bit lenght: " + payloadLength);
			offset += 8;
		}

		byte[] key = { buffer[offset], buffer[offset + 1], buffer[offset + 2], buffer[offset + 3] };
		offset += 4;
		System.out.println(
				"key: " + (key[0] & 0xff) + "," + (key[1] & 0xff) + "," + (key[2] & 0xff) + "," + (key[3] & 0xff));

		switch (opcode) {
		case 0x0: // continuation frame
			System.out.println("continuation frame");
			break;
		case 0x1: // text frame
			String text = new String();
			for (int i = 0; i < payloadLength; i++) {
				text += (char) (buffer[offset + i] ^ key[i % 4]);
			}
			System.out.print("text frame: '" + text + "'");
			if ("ping".equals(text)) {
				try {
					OutputStream outStream = socket.getOutputStream();
					sendData(outStream, "pong");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			break;
		case 0x2: // binary frame
			System.out.println("binary frame");
			break;
		case 0x8: // connection close
			System.out.println("close connection request");
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			socket = null;
			break;
		case 0x9: // ping
			System.out.println("ping");
			// TODO implement pong
			break;
		case 0xa: // pong
			// ignore
			System.out.println("pong");
			break;
		}
	}

	private void sendData(OutputStream out, String data) throws IOException {
//		System.out.println("sending: " + data);
		out.write(0b10000001); // FIN and opcode = 0x1
		if (data.length() < 126) {
			out.write(data.length() & 0x7f); // mask = 0, length in one byte
		} else if (data.length() < 0xffff) {
			out.write(0x7e); // mask = 0, length in following two bytes
			out.write(data.length() >> 8); // write high byte of length
			out.write(data.length() & 0xff); // write low byte of length
		} else {
			out.write(0x7f); // mask = 0, length in following 8 bytes
			out.write(data.length() >> 56);
			out.write((data.length() >> 48) & 0xff);
			out.write((data.length() >> 40) & 0xff);
			out.write((data.length() >> 32) & 0xff);
			out.write((data.length() >> 24) & 0xff);
			out.write((data.length() >> 16) & 0xff);
			out.write((data.length() >> 8) & 0xff);
			out.write(data.length() & 0xff);
		}
		out.write(data.getBytes());
	}

	public void doHeaders(PrintWriter out, BufferedReader in) throws Exception {
		String inputLine = null;
		String key = null;

		// Read the headers
		while ((inputLine = in.readLine()) != null) {
			System.out.println("received: " + inputLine);
			// Get the key
			if (inputLine.startsWith("Sec-WebSocket-Key"))
				key = inputLine.substring("Sec-WebSocket-Key: ".length());

			// They're done
			if (inputLine.equals(""))
				break;
		}

		// We need a key to continue
		if (key == null)
			throw new Exception("No Sec-WebSocket-Key was passed!");

		// Send our headers
		System.out.println("sending out headers");
		out.println("HTTP/1.1 101 Switching Protocols\r");
		out.println("Upgrade: websocket\r");
		out.println("Connection: Upgrade\r");
		out.println("Sec-WebSocket-Accept: " + createOK(key) + "\r");
		out.println("\r");
	}

	public String createOK(String key) throws NoSuchAlgorithmException, UnsupportedEncodingException, Exception {
		String uid = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
		String text = key + uid;

		MessageDigest md = MessageDigest.getInstance("SHA-1");
		byte[] sha1hash = new byte[40];
		md.update(text.getBytes("iso-8859-1"), 0, text.length());
		sha1hash = md.digest();

		return new String(base64(sha1hash));
	}

	public byte[] base64(byte[] bytes) throws Exception {
		ByteArrayOutputStream out_bytes = new ByteArrayOutputStream();
		OutputStream out = new Base64.OutputStream(out_bytes); // Using
																// http://iharder.net/base64
		out.write(bytes);
		out.close();
		return out_bytes.toByteArray();
	}
}