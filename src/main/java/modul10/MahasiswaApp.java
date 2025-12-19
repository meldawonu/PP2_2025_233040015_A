package modul10;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class MahasiswaApp extends JFrame {

  // komponen GUI
  JTextField txtNama, txtNim, txtJurusan, txtCari;
  JButton btnSimpan, btnEdit, btnHapus, btnClear, btnCari;
  JTable tableMahasiswa;
  DefaultTableModel Model;

  public MahasiswaApp() {
    // setup frame
    setTitle("Aplikasi CRUD Mahasiswa JDBC");
    setSize(600, 500);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);
    setLayout(new BorderLayout());

    // 1. panel form (input data)
    JPanel panelForm = new JPanel(new GridLayout(4, 2, 10, 10));
    panelForm.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    panelForm.add(new JLabel("Nama:"));
    txtNama = new JTextField();
    panelForm.add(txtNama);

    panelForm.add(new JLabel("NIM:"));
    txtNim = new JTextField();
    panelForm.add(txtNim);

    panelForm.add(new JLabel("Jurusan:"));
    txtJurusan = new JTextField();
    panelForm.add(txtJurusan);

    // panel tombol
    JPanel panelTombol = new JPanel(new FlowLayout());
    btnSimpan = new JButton("Simpan");
    btnEdit = new JButton("Edit");
    btnHapus = new JButton("Hapus");
    btnClear = new JButton("Clear");
    btnCari = new JButton("Cari");
    txtCari = new JTextField(12);

    panelTombol.add(btnSimpan);
    panelTombol.add(btnEdit);
    panelTombol.add(btnHapus);
    panelTombol.add(btnClear);
    panelTombol.add(new JLabel("Cari Nama:"));
    panelTombol.add(txtCari);
    panelTombol.add(btnCari);

    // Gabungkan panel form dan tombol di bagian atas (NORTH)
    JPanel panelAtas = new JPanel(new BorderLayout());
    panelAtas.add(panelForm, BorderLayout.CENTER);
    panelAtas.add(panelTombol, BorderLayout.SOUTH);
    add(panelAtas, BorderLayout.NORTH);

    // 2. Table data (menampilkan data)
    Model = new DefaultTableModel();
    Model.addColumn("No");
    Model.addColumn("Nama");
    Model.addColumn("NIM");
    Model.addColumn("Jurusan");

    tableMahasiswa = new JTable(Model);
    JScrollPane scrollPane = new JScrollPane(tableMahasiswa);
    add(scrollPane, BorderLayout.CENTER);

    // ---Event listeners---

    // Listener Klik Tabel (untuk mengambil data saat baris diklik)
    tableMahasiswa.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        int selectedRow = tableMahasiswa.getSelectedRow();
        if (selectedRow >= 0) {
          txtNama.setText(Model.getValueAt(selectedRow, 1).toString());
          txtNim.setText(Model.getValueAt(selectedRow, 2).toString());
          txtJurusan.setText(Model.getValueAt(selectedRow, 3).toString());
        }
      }
    });

    // aksi tombol simpan(create)
    btnSimpan.addActionListener(e -> tambahData());

    // aksi tombol edit (update)
    btnEdit.addActionListener(e -> ubahData());

    // aksi tombol hapus (delete)
    btnHapus.addActionListener(e -> hapusData());

    // aksi tombol clear
    btnClear.addActionListener(e -> kosongkanForm());

    // aksi tombol cari
    btnCari.addActionListener(e -> cariData());

    // load data saat aplikasi pertama kali jalan
    loadData();
  }

  // ---logika CRUD---
  // Method cari data berdasarkan nama
  private void cariData() {
    String keyword = txtCari.getText().trim();
    Model.setRowCount(0);
    if (keyword.isEmpty()) {
      loadData();
      return;
    }
    try {
      Connection conn = KoneksiDB.configDB();
      String sql = "SELECT * FROM mahasiswa WHERE nama LIKE ?";
      PreparedStatement pst = conn.prepareStatement(sql);
      pst.setString(1, "%" + keyword + "%");
      ResultSet rs = pst.executeQuery();
      int no = 1;
      while (rs.next()) {
        Model.addRow(new Object[] {
            no++, rs.getString("nama"), rs.getString("nim"), rs.getString("jurusan")
        });
      }
    } catch (Exception e) {
      JOptionPane.showMessageDialog(this, "Gagal Cari Data: " + e.getMessage());
    }
  }

  // 1. READ (menampilkan Data)
  private void loadData() {
    Model.setRowCount(0); // Reset tabel
    try {
      Connection conn = KoneksiDB.configDB();
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery("SELECT * FROM mahasiswa");

      // kosongkan tabel sebelum mengisi data
      int no = 1;
      while (rs.next()) {
        Model.addRow(new Object[] {
            no++,
            rs.getString("nama"),
            rs.getString("nim"),
            rs.getString("jurusan")
        });
      }
    } catch (Exception e) {
      JOptionPane.showMessageDialog(this, "Gagal Load Data: " + e.getMessage());
    }
  }

  // 2. CREATE (menambah data)
  private void tambahData() {
    // Validasi inputan
    if (txtNama.getText().trim().isEmpty() || txtNim.getText().trim().isEmpty()) {
      JOptionPane.showMessageDialog(this, "Data tidak boleh kosong!");
      return;
    }
    try {
      Connection conn = KoneksiDB.configDB();
      // Cek apakah NIM sudah ada
      String cekSql = "SELECT COUNT(*) FROM mahasiswa WHERE nim = ?";
      PreparedStatement cekPst = conn.prepareStatement(cekSql);
      cekPst.setString(1, txtNim.getText());
      ResultSet rs = cekPst.executeQuery();
      if (rs.next() && rs.getInt(1) > 0) {
        JOptionPane.showMessageDialog(this, "NIM sudah terdaftar! Input ditolak.");
        return;
      }
      // Jika NIM belum ada, lakukan INSERT
      String sql = "INSERT INTO mahasiswa (nama, nim, jurusan) VALUES (?, ?, ?)";
      PreparedStatement pst = conn.prepareStatement(sql);
      pst.setString(1, txtNama.getText());
      pst.setString(2, txtNim.getText());
      pst.setString(3, txtJurusan.getText());
      pst.execute();
      JOptionPane.showMessageDialog(this, "Data Berhasil Disimpan");
      loadData();
      kosongkanForm();
    } catch (Exception e) {
      JOptionPane.showMessageDialog(this, "Gagal Simpan Data: " + e.getMessage());
    }
  }

  // 3. UPDATE (mengubah data berdasarkan NIM)
  private void ubahData() {
    try {
      String sql = "UPDATE mahasiswa SET nama = ?, jurusan = ? WHERE nim = ?";
      Connection conn = KoneksiDB.configDB();
      PreparedStatement pst = conn.prepareStatement(sql);

      pst.setString(1, txtNama.getText());
      pst.setString(2, txtJurusan.getText());
      pst.setString(3, txtNim.getText());

      pst.execute();
      JOptionPane.showMessageDialog(this, "Data Berhasil Diubah");
      loadData();
      kosongkanForm();
    } catch (Exception e) {
      JOptionPane.showMessageDialog(this, "Gagal Ubah Data: " + e.getMessage());
    }
  }

  // 4. DELETE (menghapus data )
  private void hapusData() {
    try {
      String sql = "DELETE FROM mahasiswa WHERE nim = ?";
      Connection conn = KoneksiDB.configDB();
      PreparedStatement pst = conn.prepareStatement(sql);

      pst.setString(1, txtNim.getText());

      pst.execute();
      JOptionPane.showMessageDialog(this, "Data Berhasil Dihapus");
      loadData();
      kosongkanForm();
    } catch (Exception e) {
      JOptionPane.showMessageDialog(this, "Gagal Hapus Data: " + e.getMessage());
    }
  }

  private void kosongkanForm() {
    txtNama.setText(null);
    txtNim.setText(null);
    txtJurusan.setText(null);
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new MahasiswaApp().setVisible(true));
  }

}