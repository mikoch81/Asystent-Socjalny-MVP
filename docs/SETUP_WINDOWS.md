# SETUP_WINDOWS

## Opcja A – masz już lokalny folder i repo
1. Otwórz repo lokalnie:

```powershell
cd "C:\Users\Michał\Asystent MOPS"
```

2. Skopiuj do niego pliki startera.

3. Utwórz branch:

```powershell
git checkout -b feature/bootstrap-starter
```

4. Commit i push:

```powershell
git add .
git commit -m "bootstrap starter for Asystent Socjalny MVP"
git push -u origin feature/bootstrap-starter
```

## Opcja B – klon od zera
```powershell
cd "C:\Users\Michał"
git clone https://github.com/mikoch81/Asystent-Socjalny-MVP.git "Asystent MOPS"
```

## Następnie
- otwórz projekt w Android Studio,
- zsynchronizuj Gradle,
- odpal aplikację.
