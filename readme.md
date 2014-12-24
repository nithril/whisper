
user connects to the site
it creates a public/private key
public key is sent to the server


user connects to a channel
all public key are sent to the user



stomp ?

/user/ID_USER ?


envoie des messages aux users d'un channel ?


User cree un channel, couple clé privé/clé public

Utilisateur se connecte à un channel



User connecté à un channel possède la liste des clés public des users connectés sur ce channel

User envoie un message sur un channel:
Envoie à la queue /queue/CHANNEL_PRIVATE_ID avec un content par personne du channel crypté avec la public key
Le serveur dispatch au user connecté /queue/USER_PRIVATE_ID

User veut rejoindre un channel
Utilisation du CHANNEL_PUBLIC_ID pour faire une requete d'accès
Si channel public, ajout du user à la liste des users authorisé du channel
si succède envoie du CHANNEL_PRIVATE_ID


Echange des clés public/privé
Les users connectés au channel doivent s'echanger leurs clés publics






