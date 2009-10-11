require 'rake'#
require 'fileutils'
require 'erb'
# git tag -am 'Rolled the annotated version' 1.0-a
# This might soon integrate with ProjectSprouts

# Basic Rake tasks for compiling Flex, if you have ruby and everything setup
# This will be automated in the future (installing ruby and such)

# VARIABLES
FLEX_BUILDER_PROJECTS = File.expand_path("~/Documents/Flex Builder 3").gsub(" ", "\ ")
FLEX_SDK = File.expand_path(".")
TRUNK = "http://opensource.adobe.com/svn/opensource/flex/sdk/trunk"
SANDBOX = "http://opensource.adobe.com/svn/opensource/flex/sdk/sandbox/viatropos/trunk"
SVN_STATUS = /(.)\s*(.*)/
THIS = File.expand_path(File.dirname(__FILE__))
# This is a special variable.
# Because subversion 1.4 doesn't have merge tracking,
# this will be rewritten every time you run the merge command in here
# LAST_MERGE = 10955
LAST_MERGE_PATTERN = /\#\s*LAST_MERGE\s*\=\s*(\d+)/

# Helper Methods
def message
  # soon this will be a template once i figure that out
end

def get_last_merge
  last_merge = nil
  File.open(__FILE__, "r+") do |this|
    this.readlines.each do |line|
      if line.match(LAST_MERGE_PATTERN)
        last_merge = $1
        break
      end
    end
  end
  last_merge
end

def template(target_path, template_path)
  FileUtils.touch(target_path) unless File.exists?(target_path)
  File.truncate(target_path, 0)
  File.open(target_path, 'r+') do |f|
    # parse the template file into this
    f.print ERB.new(IO.read(template_path), nil, '-').result(binding) 
  end
end

def modified
  files = []
  %x[svn status].each do |line|
    line.gsub!(SVN_STATUS) do |match|
      if $1 == "M"
        files << $2 # $2 is the file name
      end
    end
  end
  files
end

def get_revisions_to_merge
  revisions = []
  last_merge = get_last_merge.to_i
  %x[svn log -q #{TRUNK}].each do |logging|
    if logging =~ /^r(\d+)/
      num = $1.to_i
      break if num <= last_merge
      revisions << num
    end
  end
  revisions
end

def revision
  revision = 'unknown'
  %x[svn info].each do |line|
    if line.match(/Revision\:\s*(.*)/)
      revision = $1
      break
    end
  end
  revision
end

desc "Commit to svn and git"
task :push do
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
    file = "svn-commit-template-tempfile.txt"
    @revision = revision
    @files = modified
    begin
      # eventually I will use Process and Kernel, but haven't learned that yet
      FileUtils.touch("#{file}") unless File.exists?("#{file}")
      template("#{file}", "svn-commit-template.txt.erb")
      $stdout.print "Please write your commit message.  Press Enter when complete\n"
      system("vi #{file}") unless
        system("edit #{file}") unless
          system("mate #{file}")
      $stdout.flush
      $stdin.gets.chomp
      message = IO.read(file)
      raise 'stop: ' + message.to_s
      system("svn commit -m '#{message}'")
      system("git add .")
      system("git commit -a -m '#{message}'")
    rescue Exception => e
      
    end
    FileUtils.remove(file) if File.exists?(file)
  end
  
  desc "Merge remote sandbox with remote trunk"
  task :merge_remote do
    system("svn merge #{TRUNK} #{SANDBOX}")
  end
  
  desc "Merge remote trunk with local working copy"
  task :merge_local do
    newer = ENV.include?("new") ? ENV["new"] : "HEAD"
    older = ENV.include?("old") ? ENV["old"] : get_last_merge
    
    revisions = get_revisions_to_merge.reverse.collect {|r| r.to_s}
    revisions.each do |r|
      puts "executing >> svn merge -r #{older}:#{r} #{TRUNK} ."
      system("svn merge -r #{older}:#{r} #{TRUNK} .")
      puts "completed merge"
      conflicts = %x[grep -r "<<<<<<<[^<]" ./frameworks/projects]#.delete_if {|x| x =~ /^Binary/}
      if conflicts and conflicts.length > 1
        puts "Please fix the conflicts,"
        puts "set the LAST_MERGE = #{r},"
        puts "and rerun 'rake svn:merge_local'"
        puts ""
        puts conflicts.is_a?(Array) ? conflicts.join("\n") : conflicts.to_s
        puts ""
        raise "exited"
      end
      older = r
    end
    
    #line.gsub("LAST_MERGE = #{match}", "LAST_MERGE = #{revision}")
    message = "merged with trunk: #{older.to_s} => #{newer.to_s}"
    #system("svn commit -m '#{message}'")
    puts message
  end
  
  desc "Merge remote trunk with local working copy"
  task :info do
    last_merge = get_last_merge
    current_revision = "\n#{%x[svn info][0..-2]}" # capture command output
    current_revision << "Last Merge: #{last_merge}\n"
    current_revision << "Execute all 'rake svn:' tasks from: #{THIS}\n\n"
    puts current_revision
  end
  
  desc "Add modified, helper method"
  task :add_m do
    files = modified
    if !files.empty?
      system("svn add #{files.join(' ')}")
    end
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
  
  desc "The revision numbers in an array since merge"
  task :since_merge do
    revisions = get_revisions_to_merge.reverse
    puts "Merge the following revisions:"
    puts revisions.join(", ")
    revisions
  end
  
  task :todo do
    puts "-------------------------------------"
    puts "Run the following commands: "
    puts "svn commit"
    puts "rake svn:update"
    puts "rake svn:merge_local"
    puts ""
    puts "If you get the following error,"
    puts "just keep running the command over again:"
    puts ""
    puts "svn: Could not get content-type from response"
    puts ""
    puts "If it keeps saying 'Aborting commit', run this:"
    puts ""
    puts "svn resolved -R"
    puts "-------------------------------------"
  end
end