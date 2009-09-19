require 'rake'

# Basic Rake tasks for compiling Flex, if you have ruby and everything setup
# This will be automated in the future (installing ruby and such)

# VARIABLES
FLEX_BUILDER_PROJECTS = File.expand_path("~/Documents/Flex Builder 3").gsub(" ", "\ ")
FLEX_SDK = File.expand_path(".")

namespace :flex do
  namespace :compile do
    desc "Compile Complete Flex SDK"
    task :sdk do
      system("ant -q main")
    end
    
    desc "Compile Spark"
    task :spark do
      system("ant -q spark-compile")
    end
    
    desc "Compile Core Framework"
    task :mx do
      system("ant -q framework-compile")
    end
    
    desc "Compile Text Layout Framework"
    task :tlf do
      system("cd frameworks/projects/textLayout && ant -q compile && cd #{FLEX_SDK}")
    end
    
    desc "Run check-in Tests"
    task :tests do
      system("ant -q checkintests")
    end
  end
end