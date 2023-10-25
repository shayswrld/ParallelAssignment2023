#Makefile with multiple files

JAVAC =/usr/bin/javac
JAVA = /usr/bin/java
.SUFFIXES: .java .class
SRCDIR=src
BINDIR=bin


$(BINDIR)/MonteCarloMini/%.class: $(SRCDIR)/MonteCarloMini/%.java
	$(JAVAC) -d $(BINDIR)/ -cp $(BINDIR) -sourcepath $(SRCDIR) $<
	
CLASSES=MonteCarloMini/TerrainArea.class MonteCarloMini/SearchParallel.class MonteCarloMini/MonteCarloMinimizationParallel.class

CLASS_FILES=$(CLASSES:%.class=$(BINDIR)/%.class)

default: $(CLASS_FILES)
	
clean:
	rm $(BINDIR)/MonteCarloMini/*.class
	
run: $(CLASS_FILES)	
	$(JAVA) -cp bin MonteCarloMini/MonteCarloMinimizationParallel $(ARGS)
	$(JAVA) -cp bin MonteCarloMini/MonteCarloMinimizationParallel $(ARGS)
	$(JAVA) -cp bin MonteCarloMini/MonteCarloMinimizationParallel $(ARGS)
	$(JAVA) -cp bin MonteCarloMini/MonteCarloMinimizationParallel $(ARGS)
	$(JAVA) -cp bin MonteCarloMini/MonteCarloMinimizationParallel $(ARGS)
	$(JAVA) -cp bin MonteCarloMini/MonteCarloMinimizationParallel $(ARGS)
	$(JAVA) -cp bin MonteCarloMini/MonteCarloMinimizationParallel $(ARGS)
	$(JAVA) -cp bin MonteCarloMini/MonteCarloMinimizationParallel $(ARGS)
	
	
