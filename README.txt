
Dateien löschen:

1) Datei im working tree löschen (also auch von der Festplatte): (unmodified -> modified -> staged -> untracked ?)
"rm filename"
	-> Damit sind die Änderungen aber noch nicht staged
Also rufen wir "git rm filename" auf
Und dann "commit" um die Änderungen zu committen
(im Falle eines Remote Repositories kommt hier noch das "push" hinzu, damit die Dateien auch auf dem Remote-Repository gelöscht werden!)
rm filename -> git rm filename -> git commit (-> git push)


2) Datei NICHT im working tree löschen, aber aus git rausnehmen (z.B. die .iml-Files): (unmodified -> untracked ?)
"git rm --cached filename"
Und dann "commit" um die Änderungen zu committen
(im Falle eines Remote Repositories kommt hier noch das "push" hinzu!)
git rm --cached filename -> git commit -> (git push)

Es ist also nicht nötig die Datei im gitlab-Tool zu löschen, sofern die lokale Änderungen (in dem Fall die Löschung) gepusht werden.
