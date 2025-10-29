package p6;

import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
public class tugas1 {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
           public void run() {
            JFrame frame = new JFrame();
            frame.setTitle("Tugas 1: Komponen Gui Dasar");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 200);
            JButton center = new JButton("Center");
            JButton west = new JButton("West");
            JButton south = new JButton("South");
            JButton east = new JButton("East");
            JLabel label = new JLabel("Label");
            center.addActionListener(e -> {
                label.setText("Tombol Center diKlik!");
            });
            
            west.addActionListener(e -> {
                if (label.getText().equals("Tombol West diKlik!")) {
                    label.setText("Label");
                } else {
                label.setText("Tombol West diKlik!");
                }
            });
            south.addActionListener(e -> {
                label.setText("Tombol South diKlik!");
            });
            east.addActionListener(e -> {
                label.setText("Tombol East diKlik!");
            });
            frame.setLayout(new BorderLayout());
            frame.add(label,BorderLayout.NORTH);
            frame.add(west,BorderLayout.WEST);
            frame.add(east,BorderLayout.EAST);
            frame.add(south,BorderLayout.SOUTH);
            frame.add(center,BorderLayout.CENTER);
            frame.setVisible(true);
        } 
    });
    }
}