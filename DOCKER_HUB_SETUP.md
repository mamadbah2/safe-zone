# 🐳 Guide Docker Hub - Création des Repos

## Nouveaux Repos à Créer

Tu dois créer 7 repositories sur Docker Hub avec le préfixe `safe-zone-` :

### Liste des Repos

1. **safe-zone-frontend**
   - Description: Frontend Angular pour l'application Safe Zone
   
2. **safe-zone-eureka-server**
   - Description: Service Discovery Eureka pour Safe Zone
   
3. **safe-zone-config-service**
   - Description: Configuration Service pour Safe Zone
   
4. **safe-zone-api-gateway**
   - Description: API Gateway pour Safe Zone
   
5. **safe-zone-product-service**
   - Description: Service de gestion des produits pour Safe Zone
   
6. **safe-zone-user-service**
   - Description: Service de gestion des utilisateurs pour Safe Zone
   
7. **safe-zone-media-service**
   - Description: Service de gestion des médias pour Safe Zone

## Étapes sur Docker Hub

### Via l'interface Web :

1. Va sur https://hub.docker.com
2. Connecte-toi avec ton compte `mamadbah2`
3. Clique sur "Repositories"
4. Pour chaque repo :
   - Clique sur "Create Repository"
   - **Name** : `safe-zone-<service-name>` (ex: `safe-zone-frontend`)
   - **Description** : (voir ci-dessus)
   - **Visibility** : Public ou Private (selon tes besoins)
   - Clique sur "Create"

### Via Docker CLI (Alternatif) :

```bash
# Les repos seront créés automatiquement lors du premier push
# Il suffit de lancer le pipeline Jenkins et les repos seront créés
```

## Pourquoi ce Changement ?

### Problème Initial
Si tu as 2 projets différents :
- **Projet 1** (safe-zone) : product-service
- **Projet 2** (autre-app) : product-service

Les deux utiliseraient `mamadbah2/product-service` → **CONFLIT** ❌

### Solution avec Préfixe
Maintenant :
- **Projet 1** : `mamadbah2/safe-zone-product-service` ✅
- **Projet 2** : `mamadbah2/autre-app-product-service` ✅

**Aucun conflit !** Chaque projet a son propre espace. 🎉

## Nettoyage (Optionnel)

Si tu veux supprimer les anciens repos sans préfixe :

```bash
# Sur Docker Hub -> Repositories -> Settings -> Delete Repository
```

Ou garde-les pour un autre projet qui les utilise déjà.

## Vérification

Après avoir créé les repos, vérifie que le pipeline peut pusher :

```bash
# Dans Jenkins, lance le pipeline
# Vérifie les logs du stage "Push to Docker Hub"
# Tu devrais voir :
# ✅ docker push mamadbah2/safe-zone-frontend:latest
# ✅ docker push mamadbah2/safe-zone-product-service:latest
# etc.
```

## Tags Utilisés

Chaque image aura 2 tags :
- `:latest` → Dernière version stable
- `:${BUILD_NUMBER}` → Version spécifique (ex: `:42`)

Exemple :
- `mamadbah2/safe-zone-frontend:latest`
- `mamadbah2/safe-zone-frontend:42`

## Note Importante

⚠️ Les repos seront **créés automatiquement** lors du premier push si ton compte Docker Hub a l'auto-création activée. Tu n'es donc pas obligé de les créer manuellement avant.

Mais créer manuellement permet de :
- Ajouter des descriptions claires
- Configurer la visibilité (public/private)
- Ajouter un README
- Configurer les webhooks

## Commande Docker pour Tester Localement

```bash
# Pull une image
docker pull mamadbah2/safe-zone-frontend:latest

# Lister tes images
docker images | grep safe-zone

# Run un service
docker run -p 4200:80 mamadbah2/safe-zone-frontend:latest
```

