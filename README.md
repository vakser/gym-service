1. Run the following command in the project root (where the Dockerfile is located):

        docker build -t gym-service .

   -t gym-service → Tags the image with the name gym-service.
   . → Refers to the current directory where Dockerfile is present.

   If your JAR file is missing, ensure you build it first:

        mvn clean package -DskipTests

2. Verify the Docker Image
   After building, confirm that the image is created:

       docker images

   You should see an entry like:

   REPOSITORY       TAG       IMAGE ID       CREATED        SIZE
   gym-service      latest    abc123xyz      10 seconds ago 200MB

3. Run the Container
   Start a container from your image:

       docker run -p 8080:8080 --name gym-service gym-service

   -p 8080:8080 → Maps host port 8080 to container port 8080.
   --name gym-service → Names the container gym-service.
   gym-service → Uses the image we just built.

4. Verify the Running Container
   Check if the container is running:

       docker ps

   To start a shell in a running container, use:

       docker exec -it gym-service sh 

   To check logs:

       docker logs gym-service

   To stop the container:

       docker stop gym-service

5. Before running docker-compose, remove container

       docker rm gym-service  