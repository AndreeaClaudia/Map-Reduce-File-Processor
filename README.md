# Map-Reduce-File-Processor
Popescu Andreea-Claudia, 334CC

    Pentru operatiile de tip Map si Reduce am creat obiecte specifice
valorilor care trebuiau intoarse pentru a le procesa mai usor.
    Obiectele de tipul ReturnMap contin o lista de HashMap-uri in care
am adunat toate dictionarele pentru un anumit document si o lista
de String-uri in care am adunat listele cu cuvintele cele mai lungi
de la fiecare worker.
    Obiectele de tipul ReturnReduce contin numele documentului,
rang-ul calculat, lungimea maxima a cuvintelor si numarul de cuvinte
de lungime maxima.
    Astfel a fost mai usor sa fac un HashMap static cu cheia un String
pentru numele documentului si valoarea este un obiect de tipul ReturnMap.
    Am creat de asemenea si o lista de obiecte ReturnReduce pentru
a afisa la final toate rezultatele sortate dupa rang.

    In Tema2.java am preluat numarul de workeri din argumente si
am deschis ExecutorService-ul pentru operatiile de tip Map. Am creat
mai apoi fisierul de output si l-am deschis pentru citire cu FileWriter.
Pentru fisierul de input am folosit scanner si am pastrat intr-o
lista numele documentelor. Apoi am impartit documentele in fragmente
pentru fiecare worker, le-am trimis offset-ul, dimensiunea pe care
trebuie sa o citeasca si numele fisierului din care trebuie sa citeasca.
Am creat fiecaruia cate un task Runnable cu numele MyMapTask.
    Apoi am inchis ExecutorService-ul si m-am asigurat ca au terminat
task-urile toti workerii cu un awaitTermination.
    Am deschis un nou ExecutorService pentru operatiile de tip Reduce.
Le-am impartit task-urile, am creat cate un task Runnable cu numele
MyReduceTask pentru fiecare document de analizat. Le-am trimis
numele documentului, resultatele de la task-urile Map combinate pentru
fiecare document. Si de aceasta data am inchis ExecutorService-ul si m-am
asigurat ca s-au inchis toate cu un awaitTermination.
    La final rezultatele primite de la task-urile Reduce au fost
sortate descrescator in functie de rang cu ajutorul metodei sort si
cu un comparator.
    In Main mai apoi am scris in fisierul de output rezultatele
task-urilor Reduce.

    In MyMapTask.java am citit cu RandomAccessFile documentele.
Am citit byte cu byte fragmentele si le-am stocat intr-un StringBuffer.
Apoi am parcurs fragmentul in stanga offset-ului byte cu byte pentru a
gasi un delimitator, adica pentru a vedea daca fragmentul incepe sau nu
de la mijlocul unui cuvant si astfel sa pot avea fragmentul cu cuvintele
intregi la final.
    Am procedat la fel si in partea dreapta a fragmentului, pentru
a putea procesa si cuvintele la care ne-am oprit la jumatate conform
enuntului.
    Astfel am mai stocat un fragment din document cu cuvintele intregi.
La final am verificat daca primul cuvant din fragmentul initial coincide
cu primul cuvant din fragmentul cu cuvintele intregi. Daca este acelasi
cuvant inseamna ca nu am inceput de la jumatatea unui cuvant si il putem
procesa si pe acesta, daca nu coincide, atunci sarim peste acest cuvant,
a fost deja procesat de alt worker. Pentru restul cuvintelor le vom procesa
pe cele din fragmentul cu cuvintele intregi, conform enuntului.
    Am folosit un synchronized in pe HashMap-ul static unde se vor aduna
toate rezultatele de tip map, si am pus acolo rezultatul partial al
worker-ului curent.

    In MyReduceTask.java am preluat rezultatele operatiilor de tip Map
din HashMap-ul static resultsMap.
    Am iterat prin fiecare dictionar si in functie de lungimile cuvintelor,
adica in functie de chei, am calculat numarul total de cuvinte, rang-ul
documentului dupa formula, lungimea maxima a cuvintelor si numarul
de cuvinte care au aceasta lungime maxima.
    La final, tot cu synchronized am adaugat in lista statica
cate un obiect ReturnReduce.
    Am folosit o functie recursiva pentru fibonacci.

