import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import java.net.*;
import java.util.*;

public class Server extends JFrame implements ActionListener {
    JTextField txt_msg;
    JPanel p2;
    static Box vertical = Box.createVerticalBox();
    static DataOutputStream dout;
    JScrollPane scrollPane;

    public Server() {
        this.setLayout(null);

        JPanel p1 = new JPanel();
        p1.setLayout(null);
        p1.setBackground(new Color(7, 94, 87));
        p1.setBounds(0, 0, 350, 60);
        this.add(p1);

        p2 = new JPanel();
        p2.setLayout(new BorderLayout());

        scrollPane = new JScrollPane(p2);
        scrollPane.setBounds(0, 60, 350, 320);
        this.add(scrollPane);

        try {
            BufferedImage originalImage = ImageIO.read(new File("images/p1.png"));
            ImageIcon circularIcon = new ImageIcon(makeRoundedCornerImage(originalImage, 50));

            JLabel lbl = new JLabel("Gojo Satoru", circularIcon, JLabel.LEFT);
            lbl.setFont(new Font("Italic", Font.BOLD, 16));
            lbl.setBounds(5, 5, 150, 50);
            p1.add(lbl);

        } catch (IOException e) {
            e.printStackTrace();
        }

        txt_msg = new JTextField("Write Message Here");
        txt_msg.setBounds(5, 380, 300, 35);
        add(txt_msg);

        ImageIcon buttonIcon = new ImageIcon("images/send.png");
        JButton btn_send = new JButton(buttonIcon);
        btn_send.setBackground(new Color(7, 94, 87));
        btn_send.addActionListener(this);
        btn_send.setBounds(300, 380, 50, 35);
        add(btn_send);

        this.setLocation(200, 50);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setBackground(Color.WHITE);
        this.setSize(360, 460);
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            String msg = txt_msg.getText();

            JPanel p3 = formatLabel(msg);

            JPanel right = new JPanel(new BorderLayout());
            right.add(p3, BorderLayout.LINE_END);
            vertical.add(right);
            vertical.add(Box.createVerticalStrut(15));

            p2.add(vertical, BorderLayout.PAGE_START);

            dout.writeUTF(msg);

            txt_msg.setText("");

            repaint();
            invalidate();
            revalidate();
        } catch (IOException e1) {
            JOptionPane.showMessageDialog(this, "Error Message : " + e1.getMessage());
        }
    }

    public static JPanel formatLabel(String out) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel lbl = new JLabel(out);
        lbl.setOpaque(true);
        lbl.setFont(new Font("Arial", Font.BOLD, 14));
        lbl.setBackground(new Color(37, 211, 102));
        lbl.setBorder(new EmptyBorder(5, 5, 5, 15));
        panel.add(lbl);

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        JLabel time = new JLabel();
        time.setText(sdf.format(cal.getTime()));

        panel.add(time);

        return panel;
    }

    // Mark the method as static
    private static Image makeRoundedCornerImage(BufferedImage image, int diameter) {
        BufferedImage output = new BufferedImage(diameter, diameter, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2 = output.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Create a circular clip and draw the image inside it
        g2.setClip(new Ellipse2D.Double(0, 0, diameter, diameter));
        g2.drawImage(image, 0, 0, diameter, diameter, null);
        g2.dispose();

        return output;
    }

    public static void main(String[] args) {
        Server s = new Server();

        try {
            int server_port_no = 8888;
            ServerSocket ss = new ServerSocket(server_port_no);

            Socket s_socket = ss.accept();

            DataInputStream din = new DataInputStream(s_socket.getInputStream());
            dout = new DataOutputStream(s_socket.getOutputStream());

            while (true) {
                String msg_from_client = din.readUTF();
                JPanel panel = formatLabel(msg_from_client);

                if (msg_from_client.equalsIgnoreCase("BYE")) {
                    dout.writeUTF("BYE");
                    System.exit(0);
                    break;
                }

                JPanel left = new JPanel(new BorderLayout());
                left.add(panel, BorderLayout.LINE_START);
                vertical.add(left);

                s.validate();

                // Scroll to the bottom after adding the new message from the client
                SwingUtilities.invokeLater(() -> {
                    s.scrollPane.getVerticalScrollBar().setValue(s.scrollPane.getVerticalScrollBar().getMaximum());
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(s, "Error Message : " + e.getMessage());
        }
    }
}
