package Vista;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.EventQueue;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseEvent;

/**
 * 
 * @author david
 *
 */
/**
 * Clase ServidorCentral que alberga las entradas que se venden
 */
public class ServidorCentral {

	private JFrame frame;
	private JLabel labelEntradas = new JLabel("5000");
	private JLabel labelGeneral = new JLabel("3500");
	private JLabel labelVip = new JLabel("1000");
	private JLabel labelFront = new JLabel("500");
	private JLabel labelDinero = new JLabel("0");
	private static ServerSocket serverSocket;
	private static Socket socket;
	private static BufferedReader bufferedReader;

	/**
	 * Método principal que inicia la aplicación.
	 * 
	 * @param args Argumentos de la línea de comandos.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ServidorCentral window = new ServidorCentral();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Constructor clase ServidorCentral
	 * 
	 * @exception IOException si ocurre un error de entrada/salida al comunicarse
	 *                        con el cliente.
	 */
	public ServidorCentral() {
		initialize();
		Thread thread = new Thread(() -> {
			try {
				startServer();
			} catch (IOException e) {

				e.printStackTrace();
			}
		});
		thread.start();
	}

	/**
	 * Inicializa la vista con sus elementos dispuestos
	 */
	private void initialize() {
		frame = new JFrame();

		frame.setBounds(100, 100, 606, 426);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JLabel lblNewLabel = new JLabel("ENTRADAS DISPONIBLES");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 25));
		lblNewLabel.setBounds(158, 10, 298, 76);
		frame.getContentPane().add(lblNewLabel);

		labelEntradas.setFont(new Font("Tahoma", Font.PLAIN, 25));
		labelEntradas.setBounds(325, 76, 89, 37);
		frame.getContentPane().add(labelEntradas);

		JLabel lblNewLabel_1 = new JLabel("ENTRADA GENERAL:");
		lblNewLabel_1.setFont(new Font("Times New Roman", Font.PLAIN, 15));
		lblNewLabel_1.setBounds(46, 181, 149, 37);
		frame.getContentPane().add(lblNewLabel_1);

		JLabel lblNewLabel_2 = new JLabel("TOTAL ENTRADAS:");
		lblNewLabel_2.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblNewLabel_2.setBounds(186, 89, 151, 19);
		frame.getContentPane().add(lblNewLabel_2);

		JLabel lblNewLabel_1_1 = new JLabel("ENTRADA VIP:");
		lblNewLabel_1_1.setFont(new Font("Times New Roman", Font.PLAIN, 15));
		lblNewLabel_1_1.setBounds(259, 181, 120, 37);
		frame.getContentPane().add(lblNewLabel_1_1);

		JLabel lblNewLabel_1_2 = new JLabel("FRONT STAGE:");
		lblNewLabel_1_2.setFont(new Font("Times New Roman", Font.PLAIN, 15));
		lblNewLabel_1_2.setBounds(433, 181, 149, 37);
		frame.getContentPane().add(lblNewLabel_1_2);

		labelGeneral.setFont(new Font("Tahoma", Font.PLAIN, 18));
		labelGeneral.setBounds(94, 223, 63, 19);
		frame.getContentPane().add(labelGeneral);

		JLabel lblNewLabel_3 = new JLabel("(50€)");
		lblNewLabel_3.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblNewLabel_3.setBounds(94, 158, 45, 13);
		frame.getContentPane().add(lblNewLabel_3);

		labelVip.setFont(new Font("Tahoma", Font.PLAIN, 18));
		labelVip.setBounds(286, 223, 63, 19);
		frame.getContentPane().add(labelVip);

		labelFront.setFont(new Font("Tahoma", Font.PLAIN, 18));
		labelFront.setBounds(465, 223, 63, 19);
		frame.getContentPane().add(labelFront);

		JLabel lblNewLabel_3_1 = new JLabel("(100€)");
		lblNewLabel_3_1.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblNewLabel_3_1.setBounds(286, 158, 45, 13);
		frame.getContentPane().add(lblNewLabel_3_1);

		JLabel lblNewLabel_3_1_1 = new JLabel("(150€)");
		lblNewLabel_3_1_1.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblNewLabel_3_1_1.setBounds(442, 160, 45, 13);
		frame.getContentPane().add(lblNewLabel_3_1_1);

		JLabel lblNewLabel_4 = new JLabel("FACTURACIÓN TOTAL:");
		lblNewLabel_4.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblNewLabel_4.setBounds(90, 303, 218, 25);
		frame.getContentPane().add(lblNewLabel_4);

		labelDinero.setFont(new Font("Tahoma", Font.PLAIN, 25));
		labelDinero.setBounds(325, 303, 89, 25);
		frame.getContentPane().add(labelDinero);

		JLabel lblNewLabel_5 = new JLabel("€");
		lblNewLabel_5.setFont(new Font("Tahoma", Font.BOLD, 20));
		lblNewLabel_5.setBounds(410, 303, 69, 44);
		frame.getContentPane().add(lblNewLabel_5);

	}

	/**
	 * Actualiza el número de entradas, tipo y facturación de las entradas del
	 * Servidor en base a los parámetros proporcionados.
	 * 
	 * @param entradas:   JLabel que actualiza el número TOTAL de entradas.
	 * @param tipo        JLabel que actualiza el numero de entradas de un
	 *                    determinado tipo.
	 * @param facturacion JLabel que actualiza la facturación total de las entradas.
	 * @param num         Cantidad a restar al número total de entradas y parciales
	 *                    de cada tipo .
	 * @param dinero      Cantidad a sumar a la facturación.
	 */
	private void cambiarNumEntradasYFacturacion(JLabel entradas, JLabel tipo, JLabel facturacion, int num, int dinero) {
		entradas.setText(Integer.toString(Integer.parseInt(entradas.getText()) - num));
		tipo.setText(Integer.toString(Integer.parseInt(tipo.getText()) - num));
		facturacion.setText(Integer.toString(Integer.parseInt(facturacion.getText()) + dinero));
	}

	/**
	 * Este método inicia un servidor que escucha conexiones entrantes en el puerto
	 * 1234. Cuando se establece una conexión, envía las entradas disponibles al
	 * cliente. Luego espera recibir un mensaje del cliente con el formato
	 * "cantidad,tipo" donde tipo puede ser "General", "VIP" o "Front Stage" y
	 * cantidad es 1,2 o 3. Dependiendo del tipo y cantidad de entradas solicitadas,
	 * actualiza las etiquetas de entradas y facturación correspondientes.
	 * 
	 * @throws IOException si ocurre un error de entrada/salida al comunicarse con
	 *                     el cliente.
	 */
	private void startServer() throws IOException {
		serverSocket = new ServerSocket(1234);

		while (true) {
			Socket socket = serverSocket.accept();
			OutputStream outputStream = socket.getOutputStream();
			outputStream.write((labelEntradas.getText() + "\n").getBytes());
			outputStream.write((labelGeneral.getText() + "\n").getBytes());
			outputStream.write((labelFront.getText() + "\n").getBytes());
			outputStream.write((labelVip.getText() + "\n").getBytes());
			outputStream.flush();

			bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			try {
				if (bufferedReader != null) {

					String inputLine = bufferedReader.readLine();
					String[] partes = inputLine.split(",");
					String tipo = partes[1];
					String cantidad = partes[0];

					switch (tipo) {
					case "General":
						switch (cantidad) {
						case "1":
							cambiarNumEntradasYFacturacion(labelEntradas, labelGeneral, labelDinero, 1, 50);
							break;
						case "2":
							cambiarNumEntradasYFacturacion(labelEntradas, labelGeneral, labelDinero, 2, 100);
							break;
						case "3":
							cambiarNumEntradasYFacturacion(labelEntradas, labelGeneral, labelDinero, 3, 150);
							break;

						default:
							break;
						}
						break;
					case "VIP":
						switch (cantidad) {
						case "1":
							cambiarNumEntradasYFacturacion(labelEntradas, labelVip, labelDinero, 1, 100);
							break;
						case "2":
							cambiarNumEntradasYFacturacion(labelEntradas, labelVip, labelDinero, 2, 200);
							break;
						case "3":
							cambiarNumEntradasYFacturacion(labelEntradas, labelVip, labelDinero, 3, 300);
							break;

						default:
							break;
						}
						break;
					case "Front Stage":
						switch (cantidad) {
						case "1":
							cambiarNumEntradasYFacturacion(labelEntradas, labelFront, labelDinero, 1, 150);
							break;
						case "2":
							cambiarNumEntradasYFacturacion(labelEntradas, labelFront, labelDinero, 2, 300);
							break;
						case "3":
							cambiarNumEntradasYFacturacion(labelEntradas, labelFront, labelDinero, 3, 450);
							break;

						default:
							break;
						}
						break;

					default:
						break;
					}
				} else {
					System.out.println("Todavía no ha ingresado ningún cliente");
				}
			} catch (Exception e1) {

				e1.printStackTrace();
			}

		}

	}
}