import base64, sys, io, os
sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')

base = r"C:\Users\david.6961\mercadocerto\MercadoCerto-main\startup\ux"
arqs = [
    "MercadoCerto_Trabalho2_UX_Final.docx",
    "MercadoCerto_PassoAPasso_Alteracoes.pdf",
]
for nome in arqs:
    caminho = os.path.join(base, nome)
    with open(caminho, "rb") as f:
        b64 = base64.b64encode(f.read()).decode("ascii")
    saida = os.path.join(base, nome + ".b64.txt")
    with open(saida, "w", encoding="ascii") as g:
        g.write(b64)
    print(nome, len(b64), "->", saida)
