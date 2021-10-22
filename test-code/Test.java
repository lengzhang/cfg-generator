public class Test {
  public static int test0(int a, int b) {
    int sum = 0;
    for (int i = a, j = b; i <= j && a <= b; i++, a++) {
      sum += i;
      if (a) {
        a = b;
      }
      sum += 1;
    }
    return sum;
  }

  public static int test1() {
    int I = 1, J = 2, K = 4, M = 6, N = 8, L = 0, F, G;
    int S = I + J;
    K = S + 1;
    while (I <= N) {
      K = K + N;
      F = K * N;
      I = I + 1;
    }
    if ((K + J) != (M + F)) {
      F = M + N;
      G = M * N;
    } else {
      F = M - N;
      G = M + N;
    }
    do {
      K = G * M;
      F = G - M;
      N = N + 1;
    } while (N <= 20);
    if (N < F) {
      G = H + N;
    }
    return G;
  }

  public static int test2() {
    int I = 1, J = 2, K = 4, M = 6, N = 8, L = 0, F, G;
    int S = I + J;
    K = S + 1;
    while (I <= N && J < 10) {
      K = K + N;
      F = K * N;
      I = I + 1;
      if (J < M) {
        J = J + 2;
      } else {
        J = J + 1;
      }
    }
    if ((K + J) != (M + F)) {
      F = M + N;
      G = M * N;
      do {
        K = G * M;
        F = G - M;
        N = N + 1;
      } while (N <= 20);
    } else {
      F = M - N;
      if (F < M || G < 20) {
        G = M + N;
      } else {
        G = M - N;
      }
    }
    do {
      K = G * M;
      F = G - M;
      N = N + 1;
    } while (N <= 20);
    if (N < F) {
      G = H + N;
    }
    return G;
  }

  public static int test3() {
    int I = 1, J = 2, K = 4, M = 6, N = 8, L = 0, F, G;
    int S = I + J;
    K = S + 1;
    if ((K + J) != (M + F)) {
      F = M + N;
      G = M * N;
      while (I <= N && G < (F + I)) {
        K = K + N;
        F = K * N;
        I = I + 1;
      }
    }
    elseF = M - N;
    do {
      K = G * M;
      if (N < F) {
        G = H + N;
      }
      F = G - M;
      N = N + 1;
    } while (N <= 20);
    return G;
  }

  public static int test4() {
    int I = 1, J = 2, K = 4, M = 6, N = 8, L = 0, F = 1, G;
    int S = I + J;
    K = S + 1;
    while (I <= N) {
      K = K + N;
      do {
        if ((K + J) != (M + F)) {
          G = M * N;
          F = F + 1;
        } else
          F = F + 2;
      } while (F <= 20);
      F = K * N;
      I = I + 1;
    }
    if ((K + J) != (M + F)) {
      F = M + N;
      G = M * N;
    } else {
      F = M - N;
      G = M + N;
    }
    G = H + N;
    return G;

  }
}
