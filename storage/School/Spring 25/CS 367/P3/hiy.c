/* This is the only file you will be editing and submit.
 * - Copyright of Starter Code: Profs. Ivan Avramovic, Yutao Zhong, and Hamza Mughal
 *                              of George Mason University.  All Rights Reserved
 * - Copyright of Student Code: You!  
 * - Restrictions on Student Code: Do not post your code on any public site (eg. Github).
 * -- Use of a PRIVATE Git repository (e.g. GMU's GitLab server) is strongly recommended!
 * -- You may post your code on a PRIVATE repository to give interviewers access to it.
 * -- You are liable for the protection of your code from others.
 * - Date: Mar 2025
 */


/* CS367 Project 3, Spring 2025
 * Fill in your Name, GNumber, and Section Number in the following comment fields
 * Name:Aymen Esmael
 * GNumber:G01381276
 * Section Number: CS367-001/2             (Replace the _ with your section number)
 */




 #include <sys/wait.h>
 #include "hiy.h"
 #include "parse.h"
 #include "util.h"
 #include "logging.h"
 
 
 
 
 /* Constants */
 #define DEBUG 0 /* You can set this to 0 to turn off the debug parse information */
 #define STOP_SHELL  0
 #define RUN_SHELL   1
 
 
 /*.===============================================.
  *| Uncomment the lines below if you would like to use them:
  *+===============================================*/
 
 
 // static const char *task_path[] = { "./", "/usr/bin/", NULL };
 // static const char *instructions[] = { "quit", "help", "list", "delete", "start", "startbg", "kill", "suspend", "fg", "bg", "pipe", NULL};
 
 
 #define NUM_PATHS        ( sizeof(task_path)/sizeof(const char*) - 1 )
 #define NUM_INSTRUCTIONS ( sizeof(instructions)/sizeof(const char*) - 1 )
 
//Choosen DS is Linked List 
 typedef struct ll_node{
   int task_num;
   int exit_code;
   int status;
   pid_t PID;
   char* cmd;
   struct ll_node *next;
 }node_s;
 
 
 typedef struct ll_struct{
   int size;
   node_s *head;
   node_s *tail;
 }ll_s;
 
 
 static int task_number = 0;
 static ll_s *global_task_list = NULL;
 static pid_t current_fg_pid = -1;
 
 
 static ll_s *initiallize_ll();
 // static int add_to_ll(char *input);
 void insert(ll_s *ll, char *input);
 void manage_task(ll_s *ll);
 void manage_delete(ll_s *ll, int delete);
 static void delete_task(ll_s *ll, int pos);
 int manage_start(ll_s *ll, int task, char *input);
 static int find_task(ll_s *ll, int task, node_s **touse);
 int manage_startbg(ll_s *ll,int task, char *input);
 void manage_pipe(ll_s *ll, int task1, int task2,char *input);
 void manage_fg(ll_s *ll,int task,char *input);
 void manage_bg(ll_s *ll, int task, char *input);
 void manage_kill(ll_s *ll, int task);
 void manage_suspend(ll_s *ll, int task);
 void handle_sigchld(int sig);
 void handle_sigint(int sig);
 void handle_sigtstp(int sig);
 void handle_sigquit(int sig);
 // static void promote_to_fg(node_s *task);
 static node_s *find_next_task(ll_s *ll, int current_task_num);
 
 /*-------------------------------------------*/
 /*  The entry of your task manager program   */
 /*-------------------------------------------*/
 
 
 int main() {
     char *cmd = NULL;
     int do_run_shell = RUN_SHELL;
     ll_s *ll = initiallize_ll();
     global_task_list = ll;
 
 
     //signal handler for ^c, ^z, and ^backslash
     struct sigaction sg_child;
     memset(&sg_child,0,sizeof(sg_child));
     sg_child.sa_handler = handle_sigchld;
     sigaction(SIGCHLD,&sg_child,NULL);
 
 
     struct sigaction new,old;
     memset(&new,0,sizeof(new));
     new.sa_handler = handle_sigint;
     sigaction(SIGINT,&new,&old);
 
 
     struct sigaction new1,old1;
     memset(&new1,0,sizeof(new1));
     new1.sa_handler = handle_sigtstp;
     new1.sa_flags = SA_RESTART;
 
 
     sigaction(SIGTSTP,&new1,&old1);
 
 
     struct sigaction sa;
     memset(&sa,0,sizeof(sa));
     sa.sa_handler = handle_sigquit;
     sigaction(SIGQUIT, &sa, NULL);
    
    
     /* Intial Prompt and Welcome */
     log_hiy_intro();
     log_hiy_help();
    
 
 
     /* Shell looping here to accept user command and execute */
     while (do_run_shell == RUN_SHELL) {
       if (current_fg_pid > 0) {
         pause();    
         continue;  
     }
         char *argv[MAXARGS+1] = {0};        /* Argument list */
         Instruction inst = {0};           /* Instruction structure: check parse.h */
 
 
         /* Print prompt */
         log_hiy_prompt();
        
         /* Get Input - Allocates memory for the cmd copy */
         cmd = get_input();
         /* If the input is whitespace/invalid, get new input from the user. */
         if(cmd == NULL) {
           continue;
         }
        
         /* Parse the Command and Populate the Instruction and Arguments */
         initialize_command(&inst, argv);    /* initialize arg lists and instruction */
         parse(cmd, &inst, argv);            /* call provided parse() */
 
 
         if (DEBUG) {  /* display parse result, redefine DEBUG to turn it off */
           debug_print_parse(cmd, &inst, argv, "Main, after parse");
         }
 
 
         /* After parsing: your code to continue from here */
         /*.===============================================.
          *| - The command has been parsed and you have cmd, inst, and argv filled with data
          *| - Very highly recommended to start calling your own functions after this point.
          *+===============================================*/
 
 
        
         /*==BUILT_IN: help===*/
         if (!strcmp(inst.instruct, "help"))
           log_hiy_help();
        
         /*==BUILT_IN: list===*/  
         else if(!strcmp(inst.instruct, "list"))
           manage_task(ll);
        
         /*==BUILT_IN: delete===*/
         else if(!strcmp(inst.instruct, "delete")){
           // printf("Here going to delete\nthe value to delete is %d\n",inst.num);
           manage_delete(ll,inst.num);
           // printf("Done\n");
         }
        
         /*==BUILT_IN: start===*/
         else if(!strcmp(inst.instruct, "start"))
           manage_start(ll,inst.num,cmd);
 
 
         /*==BUILT_IN: startbg===*/
         else if (!strcmp(inst.instruct,"startbg"))
           manage_startbg(ll,inst.num,cmd);
           // void manage_startbg(ll_s *ll,int task, char *input);
 
 
         /*==BUILT_IN: pipe===*/
         else if (!strcmp(inst.instruct,"pipe"))
           manage_pipe(ll,inst.num,inst.num2,cmd);
 
 
         /*==BUILT_IN: fg===*/
         else if (!strcmp(inst.instruct,"fg"))
           manage_fg(ll,inst.num,cmd);
 
 
         /*==BUILT_IN: bg===*/
         else if (!strcmp(inst.instruct,"bg"))
           manage_bg(ll,inst.num,cmd);
 
 
          /*==BUILT_IN: kill===*/
         else if (!strcmp(inst.instruct,"kill"))
           manage_kill(ll,inst.num);
 
 
         /*==BUILT_IN: suspend===*/
         else if (!strcmp(inst.instruct,"suspend"))
           manage_suspend(ll,inst.num);
        
         /*==BUILT_IN: quit===*/
         else if (strcmp(inst.instruct, "quit") == 0){
           do_run_shell = STOP_SHELL;  /*set the main loop to exit when you finish processing it */
         }
 
         //adds to the ll
         else{
           // printf("The input is %s\n",cmd);
          insert(ll,cmd);
         }
          
           /*.===============================================.
           *| You will need to print a message when quit is entered, based on the instructions
           *| in the project spec. You can do that here; in your functions; or at the end of main.
           *+===============================================*/
          
        
        
         /*.===============================================.
          *| After your code: We cleanup before Looping to the next command.
          *| free_command WILL free the cmd, inst and argv data.
          *| Make sure you've COPIED ANY INFORMATION YOU NEED first.
          *| Hint: You can use the util.c functions for copying this information.
          *+===============================================*/
 
 
         free_command(cmd, &inst, argv);
         cmd = NULL;
     }  // end while
 
 
     log_hiy_quit();
     return 0;
}  // end main()
 
 
//helper function to initiallize the ll
static ll_s *initiallize_ll(){
  ll_s *ll = malloc (sizeof(ll_s));
  if(ll == NULL)
    return NULL;
  ll->head = NULL;
  ll->tail = NULL;
  ll->size = 0;
  
  return ll;
}
 
//function that inserts to the ll
void insert(ll_s *ll, char *input){
  node_s *node = malloc(sizeof(node_s));
  if (node == NULL) {
    return;
  }
  node->cmd = malloc(strlen(input) +1 );//input;
  if(node->cmd == NULL){
    free(node);
    return;
  }
  strcpy(node->cmd,input);
  node->exit_code = 0;
  node->PID = 0;
  node->task_num = ++task_number;
  node->status = LOG_STATE_READY;
  node->next = NULL;
 
 //adding to the head
  if(ll->size == 0){
    ll->head = node;
    ll->tail = node;
    ll->size++;
    log_hiy_task_init(node->task_num,input);
    return;
  }
  //adding anywhere else
  node_s *thead = ll->head;
  while(thead->next != NULL){
    thead = thead->next;
  }
  thead->next = node;
  ll->tail = node;
  ll->size++;
  log_hiy_task_init(node->task_num,input);
}
 
 //function that handles the command task
 void manage_task(ll_s *ll){
   log_hiy_num_tasks(ll->size);//display the inital size of the DS
 
 
   node_s *temp_head = ll->head;
   for(int i = 0; i < ll->size; i++){
 
 
     log_hiy_task_info(temp_head->task_num,
                       temp_head->cmd,
                        temp_head->status,
                        temp_head->PID,
                        temp_head->exit_code);
 
 
     temp_head = temp_head->next;
   }
 }
 
 //functin that handles delete from ll
 void manage_delete(ll_s *ll,int delete){
   node_s *thead = ll->head;
   for(int i = 0; i < ll->size; i++){
     // printf("%d == %d\n",thead->task_num, delete);
     //error where delete will not work
     if(thead->task_num == delete && (
       (thead->status == LOG_STATE_RUN_BG) ||
       (thead->status == LOG_STATE_RUN_FG) ||
       (thead->status == LOG_STATE_SUSPENDED)) ){
         log_hiy_status_error(thead->task_num,thead->status);
         return;
     }
     //Found the task that needs to be delete
     if(thead->task_num == delete){
       // printf("DID I find it?\n");
       delete_task(ll,i);//calls a helper function that deletes it
       log_hiy_delete(delete);
       return;
     }
     thead = thead->next;
   }
   log_hiy_task_num_error(delete);
 }
 
 //helper function that deletes from the ll
 static void delete_task(ll_s *ll, int pos){
   if(ll == NULL || ll->head == NULL || pos < 0 || pos > ll->size){
     return;
   }
   // printf("Yest I did\n");
   //deleting from the head
   if(pos == 0){
     ll->head = ll->head->next;
     ll->size--;
     return;
   }
   node_s *temp = ll->head;
   for(int i = 0; i < pos-1; i++){
     if (temp == NULL || temp->next == NULL) {
       // printf("Invalid position.\n");
       return;
     }
     temp = temp->next;
   }
   node_s *del_node = temp->next;
     if (del_node == NULL) {
         // printf("Invalid position.\n");
         return;
     }
 
 
     temp->next = del_node->next;
     ll->size--;
 }
 
 //a helper function that helps to find that task 
 //or logs the error accordingly
 static int find_task(ll_s *ll, int task, node_s **touse){
   *touse = NULL;
   node_s *val = NULL;
   node_s *tempHead = ll->head;
  
   for (int i = 0; i < ll->size; i++){
 
 
     if( (tempHead->task_num == task) && (
     (tempHead->status == LOG_STATE_RUN_BG) ||
     (tempHead->status == LOG_STATE_RUN_FG) ||
     (tempHead->status == LOG_STATE_SUSPENDED)) ){
  
       log_hiy_status_error(task, tempHead->status);
       return -1;
 
 
     }
 
 
     if(tempHead->task_num == task){
       val = tempHead;
       // printf("\nin the helper val->task_num = %d\n",val->task_num);
       *touse = val;
       // printf("\nin the helper touse->task_num = %d\n",touse->task_num);
       return 1;
     }
     tempHead = tempHead->next;
   }
   log_hiy_task_num_error(task);
   // printf("EXiting the helper function find_task\n");
   return -1;
   // if(!found){
   //   return;
   // }
 }
 
 //a helper functin that indicate if the command has a redirect
 static int hasredir (char *input){
   char *str = strdup(input);
   char *token = strtok(str, " ");
   int found = 0;
   // int i = 0;
   while(token != NULL){
     if(strcmp(token,"<")==0){
       // printf("found <\n");
       found = 1;
     }
     else if (strcmp(token,">")==0){
       // printf("found >\n");
       found = 1;
     }
     token = strtok(NULL, " ");
   }
   free(str);
   return found;
 }
 
 
 
 //a helper functin that gets the full command
 static void getfullcommand(char *input, char *command[]) {
    char *str = strdup(input);
   char *token = strtok(str, " ");
   int i = 0; 
 
   // Process each token starting from "<"
   while (token != NULL) {
       if (strcmp(token, "<") == 0) {
           // printf("found <\n");
           command[i++] = strdup(token);  // Store the redirection token
       } else if (strcmp(token, ">") == 0) {
           // printf("found >\n");
           command[i++] = strdup(token);  // Store the redirection token
       } else {
           // Store regular command arguments
           command[i++] = strdup(token);
       }
       token = strtok(NULL, " ");
   }
 
 
   // Null-terminate the array
   command[i] = NULL;
 
   free(str);
 }

 //handles the parse for redirect
 void parse_redirection(char *input, Instruction *inst) {
   char *str = strdup(input);
   char *token = strtok(str, " ");
  
   while (token != NULL) {
       if (strcmp(token, "<") == 0) {
           token = strtok(NULL, " ");
           if (token) inst->infile = strdup(token);
       }
       else if (strcmp(token, ">") == 0) {
           token = strtok(NULL, " ");
           if (token) inst->outfile = strdup(token);
       }
       token = strtok(NULL, " ");
   }
   free(str);
 }
 
 
 
 //a helper function that implements a task that has a redirect
 static int man_start_redir(ll_s *ll, int task, char *input, node_s *val,int runfg){
  
   Instruction inst = {0};           /* Instruction structure: check parse.h */
   parse_redirection(input, &inst);
 
 
   pid_t child = fork();
   if(!child){
    
     setpgid(0,0);
     current_fg_pid = child;
    
     if(inst.infile){
       log_hiy_redir(val->task_num,LOG_REDIR_IN,inst.infile);
       int fd = -1;
       fd = open (inst.infile,O_RDONLY);
       if (fd < 0){
         log_hiy_file_error(val->task_num,inst.infile);
         exit(1);
         return 0;
         /*There should be a log messeage
         *Since we terminated with an exit code of 1
         *See 3.5.3
         */
       }
       dup2(fd,STDIN_FILENO);
       close(fd);
     }
     if(inst.outfile){
       log_hiy_redir(val->task_num,LOG_REDIR_OUT,inst.outfile);
       int fd = -1;
       fd = open (inst.outfile,O_WRONLY | O_TRUNC | O_CREAT, 0600);
       if(fd < 0){
         log_hiy_file_error(val->task_num,inst.outfile);
         exit(1);
         return 0;
         /*There should be a log messeage
         *Since we terminated with an exit code of 1
         *See 3.5.3
         */
       }
       dup2 (fd, STDOUT_FILENO);
       close(fd);
     }
 
 
     char *argument[MAXARGS+1] = {0};
     getfullcommand(val->cmd,argument);
 

     char *path1 = "./";
     char *path2 = "/usr/bin/";
     char cmdinst1[MAXARGS+1];
     char cmdinst2[MAXARGS+1];

     snprintf(cmdinst1, sizeof(cmdinst1), "%s%s", path1, argument[0]);
     snprintf(cmdinst2, sizeof(cmdinst2), "%s%s", path2, argument[0]);

     execv(cmdinst1,argument);
     execv(cmdinst2, argument);
    
     log_hiy_start_error(val->cmd);
     exit(1);
     return 0;
   }
   else{
    if(runfg){
      val->PID = child;
      int prev_state = val->status;
      val->status = STATE_RUN_FG;
      log_hiy_status(val->task_num,val->cmd,child,prev_state, STATE_RUN_FG);
  
      int status;
      waitpid(child,&status,0);//waits for child with the same process as the parent to finish
  
  
      val->exit_code = WEXITSTATUS(status);//gets the exit code
  
  
      prev_state = val->status;
      val->status = STATE_FINISHED;
      log_hiy_status(val->task_num,val->cmd,child,prev_state, STATE_FINISHED);
      return 1;
    }
    else{
          
      val->PID = child;
      int prev_state = val->status;
      val->status = STATE_RUN_BG;
      //  printf("testing3");
      log_hiy_status(val->task_num,val->cmd,child,prev_state, STATE_RUN_BG);
      return 1;
    }
   }
 }
 
 //a function that handles the start of a task
 int manage_start(ll_s *ll, int task,char *input){
 
   node_s *val = malloc(sizeof(node_s));
   int found = find_task(ll,task,&val);//finds the task to impelements
  
   if (found != 1){
     return 0;
   }
   char *argv[MAXARGS+1] = {0};        /* Argument list */
   Instruction inst = {0};           /* Instruction structure: check parse.h */
    
   /* Parse the Command and Populate the Instruction and Arguments */
   initialize_command(&inst, argv);    /* initialize arg lists and instruction */
   parse(val->cmd, &inst, argv);  
 
 
   if(hasredir(input)){//task has redierec then calls the helper function to implement
     int x = man_start_redir(ll,task,input,val,1);
     return x;
   }
 
 
   pid_t child = fork();
   if(!child){
     setpgid(0,0);
    
    
     char *path1 = "./";
     char *path2 = "/usr/bin/";
     char cmdinst1[MAXARGS+1];
     char cmdinst2[MAXARGS+1];
 
 
     snprintf(cmdinst1, sizeof(cmdinst1), "%s%s", path1, inst.instruct);
     snprintf(cmdinst2, sizeof(cmdinst2), "%s%s", path2, inst.instruct);
 
 
     // char *argv[] = {val->cmd, NULL};
     execv(cmdinst1,argv);
     execv(cmdinst2,argv);
    
     log_hiy_start_error(val->cmd);
     exit(1);
     return 0;
   }
   else{
     val->PID = child;
     int prev_state = val->status;
     val->status = STATE_RUN_FG;
     current_fg_pid = child;
    //  printf("testing1");
     log_hiy_status(val->task_num,val->cmd,child,prev_state, STATE_RUN_FG);
 
     prev_state = val->status;
     int status;
     int pid = waitpid(child,&status,WUNTRACED);

    //  printf("Exit code is %d\n",val->exit_code);

     current_fg_pid = -1;
     if(pid == -1){
      //  printf("ERROR\n");
       return 0;
     }
     if(WIFEXITED(status)){
       val->exit_code = WEXITSTATUS(status);
       val->status = STATE_FINISHED;
     }
     else if(WIFSIGNALED(status)){
       // val->exit_code = WTERMSIG(status);
       val->status = STATE_KILLED;
     }
     else if(WIFSTOPPED(status)){
       val->exit_code = WSTOPSIG(status);
       val->status = STATE_SUSPENDED;
     }
     log_hiy_status(val->task_num,val->cmd,val->PID,prev_state,val->status);
     return 1;
   }
 }
 
 //handles the task if it was started in the background
 int manage_startbg(ll_s *ll,int task, char *input){
   node_s *val = malloc(sizeof(node_s));
   int found = find_task(ll,task,&val);
 
   if (found != 1){
     return 0;
   }
   char *argv[MAXARGS+1] = {0};        /* Argument list */
   Instruction inst = {0};           /* Instruction structure: check parse.h */
    
   /* Parse the Command and Populate the Instruction and Arguments */
   initialize_command(&inst, argv);    /* initialize arg lists and instruction */
   parse(val->cmd, &inst, argv);  
 
 
   if(hasredir(input)){
     return (man_start_redir(ll,task,input,val,0));
     // return;
   }

   pid_t child = fork();
   if(!child){
     setpgid(0,0);
    
     char *path1 = "./";
     char *path2 = "/usr/bin/";
     char cmdinst1[MAXARGS+1];
     char cmdinst2[MAXARGS+1];
 
     snprintf(cmdinst1, sizeof(cmdinst1), "%s%s", path1, inst.instruct);
     snprintf(cmdinst2, sizeof(cmdinst2), "%s%s", path2, inst.instruct);
 
     execv(cmdinst1,argv);
     execv(cmdinst2,argv);
    
     log_hiy_start_error(val->cmd);
     exit(1);
     return 0;

   }
   else{
    
     val->PID = child;
     int prev_state = val->status;
     val->status = STATE_RUN_BG;
    //  printf("testing3");
     log_hiy_status(val->task_num,val->cmd,child,prev_state, STATE_RUN_BG);

     return 1;
   } 
 }

 //a signla hander for sig child
 void handle_sigchld(int sig) {
  sigset_t block_mask, prev_mask;
  sigemptyset(&block_mask);
  sigaddset(&block_mask, SIGCHLD);
  // Block SIGCHLD during this section
  sigprocmask(SIG_BLOCK, &block_mask, &prev_mask);
 
 
   pid_t pid;
   int status;
 
 
   while ((pid = waitpid(-1, &status, WNOHANG | WUNTRACED | WCONTINUED)) > 0) {
       // Skip foreground process — let manage_fg handle it
       if (pid == current_fg_pid)
           continue;
 
 
       node_s *current = global_task_list->head;
       while (current) {
           if (current->PID == pid && current->status == STATE_RUN_BG) {
               int prev_state = current->status;
 
 
               if (WIFEXITED(status) || WIFSIGNALED(status)) {
                   current->status = STATE_FINISHED;
                   current->exit_code = WIFEXITED(status) ? WEXITSTATUS(status) : -1;
                   log_hiy_status(current->task_num, current->cmd, pid, prev_state, STATE_FINISHED);
               }
 
 
               break;
           }
           current = current->next;
       }
   }
   sigprocmask(SIG_SETMASK, &prev_mask, NULL);//unblocking the signal
 }
 
 //a fucntion that handles the pipe command
 void manage_pipe(ll_s *ll, int task1, int task2, char *input) {
   if (task1 == task2) {
     log_hiy_pipe_error(task1);
     return;
   }
 
   node_s *infile;
   node_s *outfile;
 
   int inputfile = find_task(ll, task1, &infile);
   int outputfile = find_task(ll, task2, &outfile);
 
   if (inputfile != 1 || outputfile != 1) {
     return;
   }
 
   int pipefd[2];
   if (pipe(pipefd) == -1) {
     log_hiy_file_error(task1, LOG_FILE_PIPE);
     return;
   }
 
   char *argvin[MAXARGS + 1] = {0};
   Instruction instin = {0};
   char *argvout[MAXARGS + 1] = {0};
   Instruction instout = {0};
 
   initialize_command(&instin, argvin);
   parse(infile->cmd, &instin, argvin);
 
   initialize_command(&instout, argvout);
   parse(outfile->cmd, &instout, argvout);
 
   log_hiy_pipe(task1, task2);
  
   pid_t child1 = fork();
   if (child1 == 0) {  // First child (writes to pipe)
     setpgid(0, 0);
     close(pipefd[0]);  // Close unused read end
 
 
     dup2(pipefd[1], STDOUT_FILENO);
     close(pipefd[1]);
 
 
     char *path1 = "./";
     char *path2 = "/usr/bin/";
     char cmdinst1[MAXARGS+1];
     char cmdinst2[MAXARGS+1];
 
 
     snprintf(cmdinst1, sizeof(cmdinst1), "%s%s", path1, instin.instruct);
     snprintf(cmdinst2, sizeof(cmdinst2), "%s%s", path2, instin.instruct);
 
 
     execv(cmdinst1,argvin);
     execv(cmdinst2,argvin);
    
     log_hiy_start_error(infile->cmd);
     exit(1);
     // return 0; 
   }
   infile->PID = child1;
   int prev_state1 = infile->status;
   infile->status = STATE_RUN_BG;
   log_hiy_status(infile->task_num,infile->cmd,child1,prev_state1, STATE_RUN_BG);

 
   // Parent closes write end of the pipe
   close(pipefd[1]);
 
 
   pid_t child2 = fork();
   if (child2 == 0) {  // Second child (reads from pipe)
     setpgid(0, 0);
     close(pipefd[1]);  // Close unused write end
 
 
     dup2(pipefd[0], STDIN_FILENO);
     close(pipefd[0]);
 
 
     char *path1 = "./";
     char *path2 = "/usr/bin/";
     char cmdinst1[MAXARGS+1];
     char cmdinst2[MAXARGS+1];
 
     snprintf(cmdinst1, sizeof(cmdinst1), "%s%s", path1, instout.instruct);
     snprintf(cmdinst2, sizeof(cmdinst2), "%s%s", path2, instout.instruct);
 
     execv(cmdinst1,argvout);
     execv(cmdinst2,argvout);
    
     log_hiy_start_error(outfile->cmd);
     exit(1);
   }
    // Parent closes read end of the pipe
   close(pipefd[0]);
   
   outfile->PID = child2;
   int prev_state2 = outfile->status;
   outfile->status = STATE_RUN_FG;
   log_hiy_status(outfile->task_num,outfile->cmd,child2,prev_state2, STATE_RUN_FG);
 
 
   int status2;
   waitpid(child2,&status2,0);//waits for child with the same process as the parent to finish
 
   outfile->exit_code = WEXITSTATUS(status2);//gets the exit code
 
 
   prev_state2 = outfile->status;
   outfile->status = STATE_FINISHED;
   log_hiy_status(outfile->task_num,outfile->cmd,child2,prev_state2, STATE_FINISHED);
 
 
   waitpid(child1, NULL, WNOHANG);//reap background process
 }
 
 //functino that handles fg
 void manage_fg(ll_s *ll, int task, char *input) {
   node_s *temphead = ll->head;
   int found = 0;
 
 
   while (temphead != NULL) {
     if (temphead->task_num == task) {
      //the following state will result fg to work
      //the same way as start
       if (temphead->status == LOG_STATE_READY ||
           temphead->status == LOG_STATE_FINISHED ||
           temphead->status == LOG_STATE_KILLED) {
         current_fg_pid = temphead->PID;
         manage_start(ll, task, input);
       }
       //changes from suspended to resume
       else if (temphead->status == LOG_STATE_SUSPENDED) {
         log_hiy_sig_sent(LOG_CMD_RESUME, task, temphead->PID);
 
         int prev_state = temphead->status;
         temphead->status = STATE_RUN_FG;
 
         log_hiy_status(temphead->task_num, temphead->cmd, temphead->PID, prev_state, temphead->status);
 
         // Resume the task with SIGCONT
         kill(temphead->PID, SIGCONT);

        //  printf("before wait\n");
 
 
         int status;
         int pid;
         do {
           pid = waitpid(temphead->PID, &status, WUNTRACED);  // Blocking wait
       } while (pid == -1 && errno == EINTR);
         if (pid == -1) {
          //  printf("pid is -1\n");
           printf("the error is %s\n", strerror(errno));
         }
 
         if (pid > 0) {
          //getting exit code different ways based on how the task ended
           if (WIFSTOPPED(status)) {
             // process was stopped again due to ^Z
             int prev_state = temphead->status;
             temphead->status = LOG_STATE_SUSPENDED;
             log_hiy_status(temphead->task_num, temphead->cmd, temphead->PID, prev_state, temphead->status);
           }
           else if (WIFEXITED(status)) {
               // process exited normally
               int prev_state = temphead->status;
               temphead->status = STATE_FINISHED;
               temphead->exit_code = WEXITSTATUS(status);
               log_hiy_status(temphead->task_num, temphead->cmd, temphead->PID, prev_state, temphead->status);
           }
           else if (WIFSIGNALED(status)) {
               // process was killed by a signal
               int prev_state = temphead->status;
               temphead->status = LOG_STATE_KILLED;
               log_hiy_status(temphead->task_num, temphead->cmd, temphead->PID, prev_state, temphead->status);
           }
         }
       }
       else if (temphead->status == LOG_STATE_RUN_BG) {
         kill(temphead->PID, SIGSTOP);
         log_hiy_status(temphead->task_num, temphead->cmd, temphead->PID, STATE_RUN_BG, STATE_RUN_FG);
 
         kill(temphead->PID, SIGCONT);
 
         int status;
         waitpid(temphead->PID, &status, 0);
         temphead->exit_code = WEXITSTATUS(status);

         int prev_state = temphead->status;
         temphead->status = STATE_RUN_FG;
 
         prev_state = temphead->status;
         temphead->status = STATE_FINISHED;
         log_hiy_status(temphead->task_num, temphead->cmd, temphead->PID, prev_state, temphead->status);
       }
       else if (temphead->status == LOG_STATE_RUN_FG) {
         log_hiy_status_error(task, STATE_RUN_FG);
       }

       found = 1;
       break;
     }
     temphead = temphead->next;  
   }

   if (!found) {
     log_hiy_task_num_error(task);
   }
 }
 
 
 
 //a function that handles task running in the background
 void manage_bg(ll_s *ll, int task, char *input){
   node_s *temp = ll->head;
   int found = 0;
 
 
   while(temp != NULL){
     if(temp->task_num == task){
      //behaves same way as startbg 
       if( (temp->status ==  LOG_STATE_READY) ||
         (temp->status == LOG_STATE_FINISHED) ||
         (temp->status == LOG_STATE_KILLED) ){
           manage_startbg(ll,task,input);
           return;
         }
         //will resume task in the bg if it is suspended
       if(temp->status == LOG_STATE_SUSPENDED){
         log_hiy_sig_sent(LOG_CMD_RESUME,temp->task_num,temp->PID);

         int prev_state = temp->status;
         temp->status = STATE_RUN_BG; 
 
         log_hiy_status(temp->task_num,temp->cmd,temp->PID,prev_state, temp->status);
 
         kill(temp->PID,SIGCONT);
 
         // int prev_state = STATE_SUSPENDED;
        //  temp->status = STATE_RUN_BG;
         // log_hiy_status(temp->task_num, temp->cmd, temp->PID, prev_state, temp->status);

       }
       else if(temp->status == LOG_STATE_RUN_BG){
         log_hiy_status_error(temp->task_num,STATE_RUN_BG);
         return;
       }
       else if(temp->status == LOG_STATE_RUN_FG){
         log_hiy_status_error(temp->task_num,STATE_RUN_FG);
         return;
       }
 
 
       found = 1;
       break;
     }
     temp = temp->next;
   }
 
 
   if(!found){ log_hiy_task_num_error(task); }
 }
 
 //a function that kills a running process
 void manage_kill(ll_s *ll, int task){
   node_s *temp = ll->head;
   node_s *val = temp;
   int found = 0;
   while(temp!=NULL){
     if(temp->task_num == task){
       if(temp->status == LOG_STATE_RUN_FG){
         log_hiy_status_error(task,LOG_STATE_RUN_FG);
         return;
       }
       val = temp;
       found = 1;
       break;
     }
     temp = temp->next;
   }
 
   if(!found) { log_hiy_task_num_error(task); }
 
   log_hiy_sig_sent(LOG_CMD_KILL,val->task_num,val->PID);
   kill(val->PID,SIGINT);//kills the process using sigint(2)
 
 
   int prevstatus = val->status;
   val->status = LOG_STATE_KILLED;
   log_hiy_status(val->task_num,val->cmd,val->PID,prevstatus,val->status);

 }
 
 //a fucntion that suspends a task running in the bg
 void manage_suspend(ll_s *ll, int task){
   node_s *temp = ll->head;
   node_s *val = temp;
   int found = 0;
   while(temp!=NULL){
     if(temp->task_num == task){
       if((temp->status == LOG_STATE_READY) ||
         (temp->status == LOG_STATE_FINISHED) ||
         (temp->status == LOG_STATE_KILLED) ){
           log_hiy_status_error(temp->task_num,temp->status);
         }
       if(temp->status == LOG_STATE_RUN_FG){
         log_hiy_status_error(task,LOG_STATE_RUN_FG);
         return;
       }
       val = temp;
       found = 1;
       break;
     }
     temp = temp->next;
   }
   if(!found) { log_hiy_task_num_error(task); }
 
 
   log_hiy_sig_sent(LOG_CMD_SUSPEND,val->task_num,val->PID);
   kill(val->PID,SIGTSTP);//suspends it using sigtstp
 
 
   int prevstatus = val->status;
   val->status = LOG_STATE_SUSPENDED;
   log_hiy_status(val->task_num,val->cmd,val->PID,prevstatus,val->status); 
 }
 
 //gets a task that is running on the FG
 node_s *getfgnode(ll_s *ll){
   node_s *temp = global_task_list->head;
   while(temp != NULL){
     if (temp->status == LOG_STATE_RUN_FG){//is ^Z only for process that are on the forground only?????
       return temp;
     }
     temp = temp->next;
   }
  //  printf("DIDn't find anything\n");
   return NULL;
 }
 
 //signal handler for sigint
 void handle_sigint(int sig){
   log_hiy_ctrl_c();
   node_s *currfg = NULL;
   currfg = getfgnode(global_task_list);
 
 
   if(currfg){
     log_hiy_sig_sent(SIGINT,currfg->task_num,currfg->PID);
     kill(-currfg->PID,SIGINT);
 
 
     int prev_status = currfg->status;
     currfg->status = STATE_KILLED;
     log_hiy_status(currfg->task_num,currfg->cmd,currfg->PID,prev_status,currfg->status);
     // exit(0);
 

   }
 }
 
 
 void handle_sigtstp(int sig){
  
   log_hiy_ctrl_z();
   node_s *currfg =  getfgnode(global_task_list);
  //checks if the return of getfgnode
  //is valid
   if(currfg){
    //  printf("Inside the if in sigtstp fun\n");
     log_hiy_sig_sent(LOG_CMD_SUSPEND,currfg->task_num,currfg->PID);
     kill(-currfg->PID,SIGTSTP);
   }
   return;
 }
 
 //a signal handler for sigquit
 void handle_sigquit(int sig) {
   log_hiy_ctrl_bs();
   static int last_used_task_num  = 0;
   node_s *currfg = getfgnode(global_task_list);//check for FG procces
  
   if(currfg){
    //  printf("thfgcurrent task number is %d\n",currfg->task_num);
     node_s *next = find_next_task(global_task_list,currfg->task_num);
    //  printf("the next task number to run is %d\n",next->task_num);
 
     if(!next || next == currfg) return;
 
     log_hiy_sig_sent(LOG_CMD_SUSPEND,currfg->task_num,currfg->PID);
     kill(-currfg->PID,SIGTSTP);
 
     int prev_status = currfg->status;
     currfg->status = STATE_SUSPENDED;
     log_hiy_status(currfg->task_num,currfg->cmd,currfg->PID,prev_status,currfg->status);
 
     manage_fg(global_task_list,next->task_num,next->cmd);
   }
   else{
     if(global_task_list->size == 0) return;
 
     if(last_used_task_num == global_task_list->size) { last_used_task_num = 0;}
 
     node_s *first = find_next_task(global_task_list,last_used_task_num++);
     if(first) manage_fg(global_task_list,first->task_num,first->cmd);
   }
 }
 
 //a function that gets the next task to suspend for sigquit
 node_s *find_next_task(ll_s *ll, int current_task_num) {
   if (!ll || !ll->head) return NULL;
 
   node_s *curr = ll->head;
   node_s *next = NULL;
 
   // Find the next task in the list, starting from the current one
   while (curr) {
       if (curr->task_num > current_task_num) {
           next = curr;
           break;
       }
       curr = curr->next;
   }
 
   // If no task with a higher task number was found, circle back to the first task
   if (!next) {
       next = ll->head;  // Start from the first task
   }
    return next;
 }
 
 
 
 
