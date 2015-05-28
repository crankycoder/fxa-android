all:
	./gradlew build

install:
	./gradlew installDebug

clean:
	./gradlew clean
	rm -rf app/src/main/libs
	rm -rf app/src/main/obj
	rm -rf build
