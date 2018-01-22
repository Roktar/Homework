using System;
using System.Windows.Forms;

namespace MLP {
    public partial class Form1 : Form {
        int currentDataSet, loopCount;
        double learn_rate, bias;
        double[] hnet, onet, error;
        double[,] inputLayer, prevLayer, targetOuput, output, hw, ow;

        public Form1() {
            InitializeComponent();
            InitData();
            button3.Enabled = false;
        }

        private void button1_Click(object sender, EventArgs e) {
            string[] data = System.IO.File.ReadAllLines(@"testing.dat");
            if (data.Length > 0) {
                for (int k = 0; k < data.Length; k++)  {
                    string[] s2 = data[k].Split(new char[] { ' ' });

                    int j = 0;
                    foreach (var s3 in s2) {
                        if (s3.Equals(""))
                            continue;
                        try {
                            inputLayer[k, j++] = Double.Parse(s3);
                        } catch(Exception e2) {
                            logbox.AppendText(e2.Source+"\n");
                        }
                    }
                }
            }
            logbox.Clear();
            logbox2.Clear();
            logboxStatus.AppendText(" :: Testing 데이터 로드" + "\n");
            button3.Enabled = true;
        }

        private void button2_Click(object sender, EventArgs e) {
            string[] data = System.IO.File.ReadAllLines(@"training.dat");
            if (data.Length > 0) {
                for (int k = 0; k < data.Length; k++) {
                    string[] s2 = data[k].Split(new char[] { ' ' });

                    int j = 0;
                    foreach (var s3 in s2)
                        inputLayer[k, j++] = Double.Parse(s3);
                }
            }
            logbox.Clear();
            logbox2.Clear();
            logboxStatus.AppendText(" :: Tranning 데이터 로드" + "\n");
            button3.Enabled = true;
        }

        private void button3_Click(object sender, EventArgs e) {
            logbox.Clear();
            logbox2.Clear();
            Trainning();
        }

        private void button4_Click(object sender, EventArgs e) {
            logbox.Clear();
            logbox2.Clear();

            logbox.AppendText("- Input->Hidden Layer Weights \n");
            for (int i=0; i<4; i++) {
                logbox.AppendText( i + " : ");
                for (int j = 0; j < 4; j++)
                    logbox.AppendText(hw[i, j].ToString("#0.######") + "  ");
                logbox.AppendText("\n");
            }

            logbox.AppendText("- Hidden->Output Layer Weights \n");
            for (int i = 0; i < 3; i++) {
                logbox.AppendText(i + " : ");
                for (int j = 0; j < 4; j++)
                    logbox.AppendText(hw[i, j].ToString("#0.######") + "  ");
                logbox.AppendText("\n");
            }
        }

        private void InitData() {
            loopCount = currentDataSet = 0;
            bias = 1;
            learn_rate = 0.5f;
            hnet = new double[4]; // temp(in->hid)
            onet = new double[4]; // temp(hid->out)
            error = new double[75]; // save Error Rate
            hw = new double[4, 4]; // weights(in->hid)
            ow = new double[3, 4]; // weights(hid->out)
            inputLayer = new double[75, 4]; // Input Layer
            prevLayer = new double[75, 4]; // Hidden Layer
            output = new double[75, 3]; // Output Layer
            targetOuput = new double[3, 3] { { 1.0f, 0.0f, 0.0f }, { 0.0f, 1.0f, 0.0f }, { 0.0f, 0.0f, 1.0f } }; // target Values

            InitWeights();
        }

       private void InitWeights() {
            for(int i=0; i<4; i++) {
                for(int j=0; j<4; j++) {
                    Random r = new Random(i+j+DateTime.Now.Millisecond);
                    hw[i, j] = r.NextDouble() - 0.5f;
                }
            } // Input -> Hidden Weights

            for(int i=0; i<3; i++) { // 4
                for(int j=0; j<4; j++){ // 3
                    Random r = new Random(i + j + DateTime.Now.Millisecond);
                    ow[i, j] = r.NextDouble() - 0.5f;
                }
            } // Hidden -> Output Weights
        }

        private double getSigmoid(double net){
            return 1 / (1 + Math.Exp(-net)); 
        }

        private void initNet() {
            for(int i=0; i<4; i++)
                hnet[i] = onet[i] = 0.0f;
        }

        private void calc(int current) {
            initNet();

            for(int i= 0; i<4; i++) {
                for (int j = 0; j < 4; j++)
                    hnet[i] += inputLayer[current, j] * hw[i, j];
                hnet[i] += bias * 1;
                prevLayer[current, i] = getSigmoid(hnet[i]);
            }

            for (int i=0; i<3; i++){
                for(int j=0; j<4; j++)
                    onet[i] += ow[i, j] * prevLayer[current, j];

                onet[i] += bias * 1;
                output[current, i] = getSigmoid(onet[i]);
            } 
        }

        private double Delta(double target, double output) {
            return (target - output) * output * (1 - output);
        }

        private double backPropagation(int current) {
            double error = 0.0f;
            double[] out_deltaValue = new double[3], prev_deltaValue = new double[4];

            int idx = (current > 48 ? 2 : (current > 24 ? 1 : 0)); // 목표데이터값 설정

            calc(current); // 레이어값 계산

            for (int i = 0; i < 3; i++) {
                out_deltaValue[i] = -(Delta(targetOuput[idx, i], output[current, i])); // 출력층 델타값 조절
                for(int j = 0; j<4; j++)
                    ow[i, j] = ow[i, j] - learn_rate * out_deltaValue[i] * prevLayer[current, j];
            } // 출력층 가중치 조절

            for(int i=0; i<3; i++) {
                for (int j=0; j<4; j++)
                    prev_deltaValue[i] += out_deltaValue[i] * ow[i, j];
            } // 은닉층 델타값 조절

            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++)
                    hw[i, j] = hw[i, j] - learn_rate * prev_deltaValue[i] * (1 - prevLayer[current, i]) * inputLayer[current, j];
            }
            // 은닉층 가중치 조절

            for (int j = 0; j < 3; j++)
                error += Math.Abs(targetOuput[idx, j] - output[current, j]);

            error /= 3.0f;

            return error;
        } // 반복하기

        private void Trainning() {
            for (int repeat = 0; repeat < (Int32.Parse(loop.Text)); repeat++) {
                loopCount++;
                error[currentDataSet] = backPropagation(currentDataSet);

                currentDataSet++;
                currentDataSet %= 75;
            }
            logboxStatus.AppendText(" :: 학습종료, 학습횟수 : " + loopCount + "\n");
            printResult();
        }

        private void Form1_Load(object sender, EventArgs e) { }

        private void printResult() {
            for (int i = 0; i < 75; i++) {
                logbox.AppendText("Data " + (i+1) + " output(value) : ");
                for (int j = 0; j < 3; j++)
                    logbox.AppendText(output[i, j].ToString("#0.######") + "  ");
                logbox.AppendText("\n");
            }

            for (int i = 0; i < 75; i++) {
                logbox2.AppendText("Data " + (i + 1) + " output(target) : ");
                for (int j = 0; j < 3; j++)
                    logbox2.AppendText(Math.Round(output[i, j], 0) + "  ");
                logbox2.AppendText("\n");
            }
        }
    }
}