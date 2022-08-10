package classes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TCPClientHandler extends Thread {

    private Socket socket;
    private Client caller;
    private BufferedReader input;

    public TCPClientHandler(Socket socket, Client caller) throws IOException {
        this.socket = socket;
        this.caller = caller;
        this.input = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
    }

    @Override
    public void run() {
        String message = "";
        caller.pintarJogo();

        while (true) {
            if (this.socket.isConnected() && this.input != null) {
                try {
                    message = this.input.readLine();
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
            } else {
                break;
            }
            if (message == null) {
                break;

            } else if ("Aguardando jogador".equals(message)) {
                caller.label.setText(message);

            } else if ("X".equals(message) || "O".equals(message)) {
                caller.tipo = message;
                caller.label.setText("Você é: " + caller.tipo);
                caller.turno = 1;

            } else if (message.charAt(0) == 'F') {

                switch (message.charAt(message.length() - 1)) {
                    case 'X':
                        if ("X".equals(caller.tipo)) {
                            caller.label.setText("Você ganhou!");
                            caller.pontos++;
                        } else {
                            caller.label.setText("Você perdeu!");
                            caller.pontosAdversario++;
                        }
                        break;
                    case 'O':
                        if ("O".equals(caller.tipo)) {
                            caller.label.setText("Você ganhou!");
                            caller.pontos++;
                        } else {
                            caller.label.setText("Você perdeu!");
                            caller.pontosAdversario++;
                        }
                        break;
                    case '-':
                        caller.label.setText("Empate!");
                        break;
                }

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(TCPClientHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
                caller.label.setText("");
                for (int i = 0; i < caller.jogo.length; i++) {
                    for (int j = 0; j < caller.jogo[0].length; j++) {
                        caller.jogo[i][j] = 0;
                    }
                }
                caller.suaPontuacao.setText(String.valueOf(caller.pontos));
                caller.pontuacaoDeles.setText(String.valueOf(caller.pontosAdversario));
                caller.apagarJogo();
                caller.pintarJogo();
                caller.sendSignal("Reiniciar");

            } else if (message.charAt(0) == 'J') {
                if (message.charAt(message.length() - 1) == 'X') {
                    int i = Integer.parseInt(String.valueOf(message.charAt(2)));
                    int j = Integer.parseInt(String.valueOf(message.charAt(4)));
                    caller.jogo[i][j] = 1;
                } else if (message.charAt(message.length() - 1) == 'O') {
                    int i = Integer.parseInt(String.valueOf(message.charAt(2)));
                    int j = Integer.parseInt(String.valueOf(message.charAt(4)));
                    caller.jogo[i][j] = 2;
                }
                caller.turno++;
                caller.atualizarJogo();

            }

        }
    }
}
