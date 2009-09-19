require 'rake'

# This might soon integrate with ProjectSprouts

# Basic Rake tasks for compiling Flex, if you have ruby and everything setup
# This will be automated in the future (installing ruby and such)

# VARIABLES
FLEX_BUILDER_PROJECTS = File.expand_path("~/Documents/Flex Builder 3").gsub(" ", "\ ")
FLEX_SDK = File.expand_path(".")
TRUNK = "http://opensource.adobe.com/svn/opensource/flex/sdk/trunk"
SANDBOX = "http://opensource.adobe.com/svn/opensource/flex/sdk/sandbox/viatropos/trunk"
SVN_STATUS = /(.)\s*(.*)/
# This is a special variable.
# Because subversion 1.4 doesn't have merge tracking,
# this will be rewritten every time you run the merge command in here
# LAST_MERGE = 10422
LAST_MERGE_PATTERN = /\#\s*LAST_MERGE\s*\=\s*(\d+)/

# Helper Methods
def message
  # soon this will be a template once i figure that out
end

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

# ENV: pass environment variables to rake task like so:
# rake svn:merge_local new=HEAD old=10301
namespace :svn do
  
  desc "Updates and merges Flex SDK with local working copy"
  task :update => ['svn:merge_remote', 'svn:merge_local'] do
    system("svn update")
  end
  
  desc "Cleanup svn"
  task :cleanup do
    system("svn cleanup")
  end
  
  desc "Updates and merges Flex SDK with local working copy"
  task :commit => ['svn:cleanup'] do
    system("svn commit -m 'merging")
  end
  
  desc "Merge remote sandbox with remote trunk"
  task :merge_remote do
    system("svn merge #{TRUNK} #{SANDBOX}")
  end
  
  desc "Merge remote trunk with local working copy"
  task :merge_local => ['svn:cleanup'] do
    File.open(__FILE__, "r+") do |this|
      newer = ENV.include?("new") ? ENV["new"] : "HEAD"
      older = nil
      this.readlines.each do |line|
        if line.match(LAST_MERGE_PATTERN)
          match = $1
          older = ENV.include?("old") ? ENV["old"] : match # the last match
          system("svn merge -r #{older}:#{newer} #{TRUNK} ..")
          line.gsub(match, newer)
          break
        end
      end
      if older.nil?
        raise "No old merge pattern (LAST_MERGE) in Rakefile"
      end
    end
    system("svn commit -m 'merged with trunk: #{older.to_s} => #{newer.to_s}'")
  end
  
  desc "Merge remote trunk with local working copy"
  task :info do
    last_merge = nil
    current_revision = "\n#{%x[svn info][0..-2]}" # capture command output
    File.open(__FILE__, "r+") do |this|
      this.readlines.each do |line|
        if line.match(LAST_MERGE_PATTERN)
          last_merge = $1
          break
        end
      end
    end
    current_revision << "Last Merge: #{last_merge}\n\n"
    puts current_revision
  end
  
  desc "Add all, helper method"
  task :add_all do
    files = []
    %x[svn status].each do |line|
      line.gsub!(SVN_STATUS) do |match|
        if $1 != "A" and $1 != "D" # if the symbol is not 'add' or 'delete'
          files << $2 # $2 is the file name
        end
      end
    end
    if !files.empty?
      puts "files: " + files.join("\n")
      # system("svn add #{files.join(' ')}")
    end
  end
end