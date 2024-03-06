package Vista;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.Font;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author david
 *
 */
/**
 * Clase Cliente que se conecta al servidor para comprar una entrada
 */
public class Cliente1 {

	private JFrame frame;
	private JTextArea textArea = new JTextArea();
	private JLabel lblTiempo = new JLabel("00:00");
	private JComboBox<String> comboBox1 = new JComboBox<>();
	private JComboBox<String> comboBox2 = new JComboBox<>();
	private static Socket socket;

	/**
	 * Método principal que inicia la aplicación.
	 * 
	 * @param args Argumentos de la línea de comandos.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				Cliente1 window = new Cliente1();
				window.frame.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * Constructor de la clase Cliente1.
	 * 
	 */
	public Cliente1() {
		initialize();

		Thread thread = new Thread(() -> {
			try {
				socket = new Socket("localhost", 1234);
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
		frame.setBounds(100, 100, 659, 427);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		comboBox1.setBounds(325, 38, 104, 21);
		comboBox1.addItem("1");
		comboBox1.addItem("2");
		comboBox1.addItem("3");
		frame.getContentPane().add(comboBox1);

		JLabel lblNewLabel = new JLabel("Cantidad Entradas:");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblNewLabel.setBounds(160, 25, 155, 41);
		frame.getContentPane().add(lblNewLabel);

		JButton confirmarButton = new JButton("Confirmar Compra");
		confirmarButton.setEnabled(false);
		confirmarButton.setFont(new Font("Tahoma", Font.PLAIN, 14));
		confirmarButton.setBounds(220, 329, 155, 29);
		frame.getContentPane().add(confirmarButton);

		JButton reservarButton = new JButton("Reservar Compra");
		reservarButton.setFont(new Font("Tahoma", Font.PLAIN, 14));
		reservarButton.setBounds(220, 283, 155, 29);
		frame.getContentPane().add(reservarButton);

		JRadioButton radioButton = new JRadioButton("Ver Entradas Disponibles");
		radioButton.setFont(new Font("Tahoma", Font.PLAIN, 15));
		radioButton.setBounds(23, 171, 191, 21);
		frame.getContentPane().add(radioButton);
		textArea.setFont(new Font("Calisto MT", Font.PLAIN, 15));

		textArea.setEditable(false);
		textArea.setBounds(220, 106, 382, 146);
		frame.getContentPane().add(textArea);

		JLabel lblNewLabel_1 = new JLabel("Tipo Entradas:");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblNewLabel_1.setBounds(190, 62, 125, 41);
		frame.getContentPane().add(lblNewLabel_1);

		comboBox2.setBounds(325, 75, 104, 21);
		comboBox2.addItem("General");
		comboBox2.addItem("VIP");
		comboBox2.addItem("Front Stage");
		frame.getContentPane().add(comboBox2);

		lblTiempo.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblTiempo.setBounds(551, 321, 72, 41);
		frame.getContentPane().add(lblTiempo);

		radioButton.addActionListener(e -> {
			if (radioButton.isSelected()) {
				textArea.setVisible(true);
				updateTextArea();
			} else {
				textArea.setVisible(false);
			}
		});

		reservarButton.addActionListener(e -> {
			JOptionPane.showMessageDialog(null,
					"Tienes 2 minutos reservada la/las entradas. Después de ese tiempo se anularán");
			reservarButton.setEnabled(false);
			confirmarButton.setEnabled(true);
			comboBox1.setEnabled(false);
			comboBox2.setEnabled(false);
			cuentaAtras(lblTiempo, reservarButton, confirmarButton, comboBox1, comboBox2);
		});

		confirmarButton.addActionListener(e -> {
			String eleccion = comboBox1.getSelectedItem().toString() + "," + comboBox2.getSelectedItem().toString();
			enviarInfo(eleccion, comboBox1.getSelectedItem().toString(), comboBox2.getSelectedItem().toString());
		});
	}

	/**
	 * Método que mediante el BufferedReades recibe las entradas que tiene el
	 * servidor y las muestra en un TextArea
	 * 
	 * @exception IOException si ocurre un error de entrada/salida al comunicarse
	 *                        con el cliente.
	 */
	private void updateTextArea() {
		try {

			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String entradasTotales = bufferedReader.readLine();
			String entradasGeneral = bufferedReader.readLine();
			String entradasVip = bufferedReader.readLine();
			String entradasFront = bufferedReader.readLine();
			textArea.setText("                                 Entradas Totales: " + entradasTotales + "\n"
					+ "Entradas Generales      Entradas VIP       Entradas Front" + "\n" + "            "
					+ entradasGeneral + "                       " + entradasVip + "                            "
					+ entradasFront);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Método que inicializa la cuenta atrás que tiene el usuario para reservar una
	 * entrada, además de habilitar/inhabilitar ciertos botones dependiendo de si
	 * llega a 0 el temporizador
	 * 
	 * @param lblTiempo:       Jlabel de la vista cuyo texto es el que irá cambiando
	 * 
	 * @param reservarButton:  botón que acciona la cuenta atrás y la reserva (se le
	 *                         llama para habilitarlo de nuevo cuando la cuenta
	 *                         atrás llegue a 0)
	 * 
	 * @param confirmarButton: botón que termina la cuenta atrás y confirma la
	 *                         reserva(se le llama para inhabilitarlo de nuevo
	 *                         cuando la cuenta atrás llegue a 0)
	 * 
	 * @param comboBox1:       comboBox que contiene la cantidad de entradas que
	 *                         quiere el usuario(se le llama para habilitarlo de
	 *                         nuevo cuando la cuenta atrás llegue a 0)
	 * 
	 * @param comboBox2:       comboBox que contiene el tipo de entradas que quiere
	 *                         el usuario(se le llama para habilitarlo de nuevo
	 *                         cuando la cuenta atrás llegue a 0)
	 */
	private void cuentaAtras(JLabel lblTiempo, JButton reservarButton, JButton confirmarButton,
			JComboBox<String> comboBox1, JComboBox<String> comboBox2) {
		new Thread(() -> {
			int segundos = 120;
			while (segundos > 0) {
				int minutos = segundos / 60;
				int seg = segundos % 60;
				String textoCuentaRegresiva = String.format("%02d:%02d", minutos, seg);
				SwingUtilities.invokeLater(() -> lblTiempo.setText(textoCuentaRegresiva));
				try {
					Thread.sleep(1000);
				} catch (InterruptedException ex) {
					Thread.currentThread().interrupt();
				}
				segundos--;

				if (segundos == 0) {
					SwingUtilities.invokeLater(() -> {
						JOptionPane.showMessageDialog(null, "Tiempo expirado");
						reservarButton.setEnabled(true);
						confirmarButton.setEnabled(false);
						comboBox1.setEnabled(true);
						comboBox2.setEnabled(true);
						lblTiempo.setText("00:00");
					});
				}
			}
		}).start();
	}

	/**
	 * Metodo que envía la informacion al Servidor y al JasperReport
	 * 
	 * @param message: string que se le envia al outputstream con la informacion del
	 *                 tipo de entrada y la cantidad que eligió el usuario(Ej: 1Vip)
	 * 
	 * @param cantidad : cantidad de entradas que eligió el usuario(Ej: 3) para
	 *                 pasarlas al JasperReport mediante el Map
	 * 
	 * @param tipo     : tipo de entrada que eligió el usuario(Vip)para pasarlas al
	 *                 JasperReport mediante el Map
	 * 
	 * @exception IOException si ocurre un error de entrada/salida al comunicarse
	 *                        con el cliente.
	 * @exception JRException si ocurre un error relacionado con el JasperReport
	 */
	private void enviarInfo(String message, String cantidad, String tipo) {
		try {
			if (socket != null && socket.isConnected()) {
				OutputStream outputStream = socket.getOutputStream();
				outputStream.write((message + "\n").getBytes());
				outputStream.flush();
				JOptionPane.showMessageDialog(null,
						"Compra realizada correctamente, ahora se le enviará el justificante de compra");

				Map<String, Object> parametro = new HashMap<>();
				switch (tipo) {
				case "General":
					parametro.put("tipo", tipo);
					parametro.put("cantidad", cantidad);
					parametro.put("precio", Integer.toString(Integer.parseInt(cantidad) * 50));
					break;
				case "VIP":
					parametro.put("tipo", tipo);
					parametro.put("cantidad", cantidad);
					parametro.put("precio", Integer.toString(Integer.parseInt(cantidad) * 100));
					break;
				case "Front Stage":
					parametro.put("tipo", tipo);
					parametro.put("cantidad", cantidad);
					parametro.put("precio", Integer.toString(Integer.parseInt(cantidad) * 150));
					break;
				default:
					break;
				}

				try {
					JasperReport jasperReport = JasperCompileManager.compileReport("Jasper\\Festival\\Blank_A4.jrxml");
					JasperPrint informePrint = JasperFillManager.fillReport(jasperReport, parametro,
							new JREmptyDataSource());
					JasperViewer.viewReport(informePrint);
					socket.close();
					frame.setEnabled(false);
				} catch (JRException e1) {
					e1.printStackTrace();
				}

			} else {
				System.out.println("Cliente no conectado");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}