artifact_name       := search.api.ch.gov.uk
version             := "unversioned"

.PHONY: all
all: build

.PHONY: clean
clean:
	mvn clean
	rm -f $(artifact_name)-*.zip
	rm -f $(artifact_name).jar
	rm -rf ./build-*
	rm -f ./build.log

.PHONY: build
build:
	mvn versions:set -DnewVersion=$(version) -DgenerateBackupPoms=false
	mvn package -DskipTests=true
	cp ./target/$(artifact_name)-$(version).jar ./$(artifact_name).jar

.PHONY: test
test: test-unit

.PHONY: test-unit
test-unit: clean
	mvn test

.PHONY: test-integration
test-integration:
	mvn integration-test -Dskip.unit.tests=true

.PHONY: package
package:
ifndef version
	$(error No version given. Aborting)
endif
	$(info Packaging version: $(version))
	mvn versions:set -DnewVersion=$(version) -DgenerateBackupPoms=false
	mvn package -DskipTests=true
	$(eval tmpdir:=$(shell mktemp -d build-XXXXXXXXXX))
	cp ./target/$(artifact_name)-$(version).jar $(tmpdir)/$(artifact_name).jar
	cd $(tmpdir); zip -r ../$(artifact_name)-$(version).zip *
	rm -rf $(tmpdir)

.PHONY: dist
dist: clean build package

.PHONY: sonar
sonar:
	mvn sonar:sonar

.PHONY: sonar-pr-analysis
sonar-pr-analysis:
	mvn sonar:sonar -P sonar-pr-analysis

.PHONY: security-check
security-check:
	mvn org.owasp:dependency-check-maven:update-only
	mvn org.owasp:dependency-check-maven:check -DfailBuildOnCVSS=4 -DassemblyAnalyzerEnabled=false

.PHONY: build-image
build-image:
	@echo "Running build-image"
	docker build --build-arg JAR_FILE=$(artifact_jar) -t $(artifact_name) .
	@echo "Finished build-image"

.PHONY: all
all: clean build build-image
	@echo "Running all"

.PHONY: run
run:
	docker run -it --rm $(artifact_name)

.PHONY: dependency-check
dependency-check:
	@ if [ -n "$(DEPENDENCY_CHECK_SUPPRESSIONS_HOME)" ]; then \
		if [ -d "$(DEPENDENCY_CHECK_SUPPRESSIONS_HOME)" ]; then \
			suppressions_home="$${DEPENDENCY_CHECK_SUPPRESSIONS_HOME}"; \
		else \
			printf -- 'DEPENDENCY_CHECK_SUPPRESSIONS_HOME is set, but its value "%s" does not point to a directory\n' "$(DEPENDENCY_CHECK_SUPPRESSIONS_HOME)"; \
			exit 1; \
		fi; \
	fi; \
	if [ ! -d "$${suppressions_home}" ]; then \
		suppressions_home_target_dir="./target/dependency-check-suppressions"; \
		if [ -d "$${suppressions_home_target_dir}" ]; then \
			suppressions_home="$${suppressions_home_target_dir}"; \
		else \
			mkdir -p "./target"; \
			git clone $(dependency_check_suppressions_repo_url) "$${suppressions_home_target_dir}" && \
				suppressions_home="$${suppressions_home_target_dir}"; \
			if [ -d "$${suppressions_home_target_dir}" ] && [ -n "$(dependency_check_suppressions_repo_branch)" ]; then \
				cd "$${suppressions_home}"; \
				git checkout $(dependency_check_suppressions_repo_branch); \
				cd -; \
			fi; \
		fi; \
	fi; \
	printf -- 'suppressions_home="%s"\n' "$${suppressions_home}"; \
	DEPENDENCY_CHECK_SUPPRESSIONS_HOME="$${suppressions_home}" "$${suppressions_home}/scripts/depcheck" --repopwd

.PHONY: security-check
security-check: dependency-check

