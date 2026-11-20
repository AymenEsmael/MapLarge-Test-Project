/* This is the only file you will be editing.
 * - wlf_sched.c (Wlf Scheduler Library Code)
 * - Copyright of Starter Code: Prof. Kevin Andrea, George Mason University. All Rights Reserved
 * - Copyright of Student Code: You!  
 * - Copyright of ASCII Art: Modified from an uncredited author's work:
 * -- https://www.asciiart.eu/animals/wolves
 * - Restrictions on Student Code: Do not post your code on any public site (eg. Github).
 * -- Feel free to post your code on a PRIVATE Github and give interviewers access to it.
 * -- You are liable for the protection of your code from others.
 * - Date: Jan 2025
 */

/* CS367 Project 1, Spring Semester, 2025
 * Fill in your Name, GNumber, and Section Number in the following comment fields
 * Name:  Aymen Esmael
 * GNumber:  G01381276
 * Section Number: CS367-001/2             (Replace the _ with your section number)
 */

/* wlf CPU Scheduling Library
                     .
                    / V\
                  / `  /
                 <<   |
                 /    |
               /      |
             /        |
           /    \  \ /
          (      ) | |
  ________|   _/_  | |
<__________\______)\__)
*/
 
/* Standard Library Includes */
#include <stdio.h>
#include <stdlib.h>
#include <signal.h>
#include <string.h>
/* Unix System Includes */
#include <unistd.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <sys/time.h>
#include <pthread.h>
#include <sched.h>
/* DO NOT CHANGE THE FOLLOWING INCLUDES - Local Includes 
 * If you change these, it will not build on Zeus with the Makefile
 * If you change these, it will not run in the grader
 */
#include "wlf_sched.h"
#include "strawhat_scheduler.h"
#include "strawhat_support.h"
#include "strawhat_process.h"
/* DO NOT CHANGE ABOVE INCLUDES - Local Includes */

/* Feel free to create any definitions or constants you like! */
static void addNode(Wlf_process_s *Node, Wlf_queue_s *schedule);
static void removeNode(Wlf_queue_s *schedule, int position);
static Wlf_process_s *remove_without_remove(Wlf_queue_s *schedule, int pos);
static void wlf_cleanup_high(Wlf_schedule_s *schedule);
static void wlf_cleanup_normal(Wlf_schedule_s *schedule);
static void wlf_cleanup_terminate(Wlf_schedule_s *schedule);
/* Feel free to create any helper functions you like! */

/*** Wlf Library API Functions to Complete ***/

/* Initializes the Wlf_schedule_s Struct and all of the Wlf_queue_s Structs
 * Follow the project documentation for this function.
 * Returns a pointer to the new Wlf_schedule_s or NULL on any error.
 * - Hint: What does malloc return on an error?
 */
Wlf_schedule_s *wlf_initialize() {
  //creats a Wlf_schedule_s dynamically
  Wlf_schedule_s *ll = malloc(sizeof(Wlf_schedule_s));

  //checks if malloc faild
  if(ll == NULL)
    return NULL;

  //creats and initiallize ll of different priority
  ll->ready_queue_high = NULL;
  ll->ready_queue_normal = NULL;
  ll->terminated_queue = NULL;

  ll->ready_queue_high = malloc(sizeof(Wlf_queue_s));
  if(ll->ready_queue_high == NULL)
    return NULL;

  ll->ready_queue_high->head = NULL;
  ll->ready_queue_high->count = 0;

  ll->ready_queue_normal = malloc(sizeof(Wlf_queue_s));
  if(ll->ready_queue_normal == NULL)
    return NULL;
  
  ll->ready_queue_normal->head = NULL;
  ll->ready_queue_normal->count = 0;

  ll->terminated_queue = malloc(sizeof(Wlf_queue_s));
  if(ll->terminated_queue == NULL)
    return NULL;

  ll->terminated_queue->head = NULL;
  ll->terminated_queue->count = 0;

  return ll;
}

/* Allocate and Initialize a new Wlf_process_s with the given information.
 * - Malloc and copy the command string, don't just assign it!
 * Follow the project documentation for this function.
 * - You may assume all arguments within data are Legal and Correct for this Function Only
 * Returns a pointer to the Wlf_process_s on success or a NULL on any error.
 */
Wlf_process_s *wlf_create(Wlf_create_data_s *data) {
  if(data == NULL)//validate the input
    return NULL;

  Wlf_process_s *Process_node = malloc(sizeof(Wlf_process_s));

  if(Process_node == NULL)//checks any issue with dynamically allocating memory
    return NULL;
  
  Process_node->age = 0;//initialize age to 0
  Process_node->next = NULL;//initialize next to NULL
  Process_node->pid = data->pid;//initialize the pid member to the pid argument
  
  Process_node->cmd = malloc(MAX_CMD + 1);
  if(Process_node->cmd == NULL)
    return NULL;
  strncpy(Process_node->cmd,data->original_cmd, MAX_CMD-1);//copies datas cmd to process_s cmd
  Process_node->cmd[MAX_CMD - 1] = '\0';
  //sets the Process_node->state
  unsigned short temp = 0x0;
  temp |= 0x8000;
  if(data->is_critical)
    temp |= 0x1800;
  if(data->is_high)
    temp |= 0x0800;
  Process_node->state = temp;

  return Process_node;
}

/* Inserts a process into the appropriate Ready Queue (singly linked list).
 * Follow the project documentation for this function.
 * - Do not create a new process to insert, insert the SAME process passed in.
 * Returns a 0 on success or a -1 on any error.
 */
int wlf_enqueue(Wlf_schedule_s *schedule, Wlf_process_s *process) {
  if(schedule == NULL || process == NULL)
    return -1;
  
  //while keeping everything the same changes bit 15 to 1, bit 14 to 0 and bit 13 to 0
  unsigned short temp = process->state;
  temp &= 0x1FFF;
  temp |= 0x8000;
  process->state = temp;

  //check if high and critical bit is 1
  int mask_critical = temp & 0x1000;
  int mask_high = temp & 0x0800;
  
  //checks if the critical and high bit is 1 or 0
  if(mask_critical || mask_high){
    //inserts to the appropriat queue and updates the count
    if(schedule->ready_queue_high->head == NULL){//if it is empty
      schedule->ready_queue_high->head = process;
      process->next = NULL;
      schedule->ready_queue_high->count++;
    }
    else{//if the queue is not empty
      Wlf_process_s *curr = schedule->ready_queue_high->head;
      while(curr->next != NULL){
        curr = curr->next;
      }
      curr->next = process;
      process->next = NULL;
      schedule->ready_queue_high->count++;
    }
  }
  else{
    if(schedule->ready_queue_normal->head == NULL){
      schedule->ready_queue_normal->head = process;
      process->next = NULL;
      schedule->ready_queue_normal->count++;
    }
    else{
      Wlf_process_s *curr = schedule->ready_queue_normal->head;
      while(curr->next != NULL){
        curr = curr->next;
      }
      curr->next = process;
      process->next = NULL;
      schedule->ready_queue_normal->count++;
    }
  }
  return 0;
}

/* Returns the number of items in a given Wlf Queue (singly linked list).
 * Follow the project documentation for this function.
 * Returns the number of processes in the list or -1 on any errors.
 */
int wlf_count(Wlf_queue_s *queue) {
  if (queue == NULL)
    return -1;
  return queue->count;
}

/* Selects the best process to run from the Ready Queues (singly linked list).
 * Follow the project documentation for this function.
 * Returns a pointer to the process selected or NULL if none available or on any errors.
 * - Do not create a new process to return, return a pointer to the SAME process selected.
 * - Return NULL if the ready queues were both empty OR if there were any errors.
 */
//double check
Wlf_process_s *wlf_select(Wlf_schedule_s *schedule) {
  if(schedule == NULL || schedule->ready_queue_high == NULL || schedule->ready_queue_normal == NULL)
    return NULL;
  
  Wlf_process_s *toreturn = NULL;
  int pos = 0;

  if(schedule->ready_queue_high->count > 0){//get the critical process if any and remove it
    Wlf_process_s *curr = schedule->ready_queue_high->head;

    while(curr != NULL){//look in the ll to find if there is a node with critical of 1
      if(curr->state & 0x1000){
        toreturn = curr;
        break;
      }
      curr = curr->next;
      pos++;
    }
    if(toreturn == NULL){//if not found just remove the first Node
      toreturn = schedule->ready_queue_high->head;
      pos = 0;
      // schedule->ready_queue_high->head = schedule->ready_queue_high->head->next;
      // schedule->ready_queue_high->count--;
    }
    removeNode(schedule->ready_queue_high,pos);
  }
  else if(schedule->ready_queue_normal->count > 0){//if there is nothing to remove from high look in normal
    toreturn = schedule->ready_queue_normal->head; //gets the first process from the normal queue
    removeNode(schedule->ready_queue_normal,0);
    // schedule->ready_queue_normal->head = schedule->ready_queue_normal->head->next;//moves the head pointer to the next, i.e remove the first elem
    // schedule->ready_queue_normal->count--;
  }//hehehehh

  if(toreturn == NULL)
    return NULL;

  toreturn->age = 0;
  toreturn->state &= 0x1FFF;
  toreturn->state |= 0x4000;
  toreturn->next = NULL;

  return toreturn;
}

static void removeNode(Wlf_queue_s *schedule, int position){
  if(schedule == NULL || schedule->head == NULL || position < 0)
    return ;
  if (position == 0){//removes the first Node
    // Wlf_process_s *temp = schedule->head;
    schedule->head = schedule->head->next;
    schedule->count--;
    // free(temp);
    return;
  }
  //removes at any index
  Wlf_process_s *ptr = schedule->head;
  // Wlf_process_s *temp;
  for(int i=1;i<position;i++){
    ptr = ptr->next;
  }
  // temp = ptr->next;
  ptr->next = (ptr->next)->next;
  schedule->count--;
  // free(temp);
}

/* Ages all Process nodes in the Ready Queue - Normal and Promotes any that are Starving.
 * If the Ready Queue - Normal is empty, return 0.  (Success if nothing to do)
 * Follow the specification for this function.
 * Returns a 0 on success or a -1 on any error.
 */
int wlf_promote(Wlf_schedule_s *schedule) {
  if(schedule->ready_queue_high == NULL || schedule->ready_queue_normal == NULL)
    return -1;
  if(schedule->ready_queue_normal->count == 0)
    return 0;
  
  Wlf_process_s *curr = schedule->ready_queue_normal->head;
  Wlf_process_s *prev = NULL;

  while(curr != NULL){
    curr->age++;//increments the age

    if(curr->age >= STARVING_AGE){//checks for starving age
      Wlf_process_s *toMove = curr;

      if(prev == NULL)
        schedule->ready_queue_normal->head = curr->next;
      else  
        prev->next = curr->next;
      
      schedule->ready_queue_normal->count--;

      curr = curr->next;
      toMove->next = NULL;
      addNode(toMove,schedule->ready_queue_high);
      continue;

    }
    prev = curr;
    curr = curr->next;
  }
  return 0;
}

static void addNode(Wlf_process_s *Node , Wlf_queue_s *schedule){
  if(schedule == NULL || Node == NULL)
    return;
  
  if(schedule->head == NULL){//addes to the head if empty
    schedule->head = Node;
    Node->next = NULL;
    schedule->count++;
    return;
  }
  Wlf_process_s *curr = schedule->head;
  while(curr->next != NULL){
    curr = curr->next;
  }
  curr->next = Node;
  Node->next = NULL;
  schedule->count++;

}

/* This is called when a process exits normally that was just Running.
 * Put the given node into the Terminated Queue and set the Exit Code 
 * - Do not create a new process to insert, insert the SAME process passed in.
 * Follow the project documentation for this function.
 * Returns a 0 on success or a -1 on any error.
 */
int wlf_exited(Wlf_schedule_s *schedule, Wlf_process_s *process, int exit_code) {
  if(schedule == NULL || process == NULL)
    return -1;
  //sets the terminated state to 1
  process->state &= 0x1FFF;
  process->state |= 0x2000;
  //sets the exit bits to the passed exit_code
  process->state &= 0xFF00;
  process->state |= exit_code;
  //adds to the terminated queue
  addNode(process,schedule->terminated_queue);

  return 0;
}

/* This is called when the OS terminates a process early. 
 * - This will either be in your Ready Queue - High or Ready Queue - Normal.
 * - The difference with wlf_exited is that this process is in one of your Queues already.
 * Remove the process with matching pid from either Ready Queue and add the Exit Code to it.
 * - You have to check both since it could be in either queue.
 * Follow the project documentation for this function.
 * Returns a 0 on success or a -1 on any error.
 */
int wlf_killed(Wlf_schedule_s *schedule, pid_t pid, int exit_code) {
  if(schedule == NULL)
    return -1;

  int found = 0;
  int pos = 0;
  Wlf_process_s *process = NULL;

  Wlf_process_s *curr = schedule->ready_queue_high->head;
  while(curr != NULL){//loop to look for the pid
    if(curr->pid == pid){
      found = 1;
      process = curr;
      break;
    } 
    pos++;
    curr = curr->next;
  }
  if(found){//remove the node that contains the pid
    process = remove_without_remove(schedule->ready_queue_high,pos);
  }
  if(!found){//if not found look in the normal queue
    pos = 0;
    curr = schedule->ready_queue_normal->head;
    while(curr != NULL){
      if(curr-> pid == pid){
        process = curr;
        break;
      }
      pos++;
      curr = curr->next;
    }
    if(process != NULL){
      process = remove_without_remove(schedule->ready_queue_normal,pos);
    }
  }

  if(process == NULL)
    return -1;

  //setting the state
  process->state &= 0x1FFF;
  process->state |= 0x2000;

  //setting the exit code for the state
  process->state &= 0xFF00;
  process->state |= exit_code;

  //adding the node to the terminated queue
  addNode(process,schedule->terminated_queue);

  return 0;
}

static Wlf_process_s *remove_without_remove(Wlf_queue_s *schedule, int pos){
  if(schedule == NULL || schedule->head == NULL)
    return NULL;
  
  if (pos == 0){//removes the first Node
    Wlf_process_s *temp = schedule->head;
    schedule->head = schedule->head->next;
    schedule->count--;
    temp->next = NULL;
    return temp;
  }
  //removes at any index
  Wlf_process_s *ptr = schedule->head;
  Wlf_process_s *temp;
  for(int i=0;i<pos-1 && ptr->next != NULL;i++){
    ptr = ptr->next;
  }
  temp = ptr->next;
  ptr->next = (ptr->next)->next;
  schedule->count--;
  temp->next = NULL;
  return temp;
}

/* This is called when StrawHat reaps a Terminated (Defunct) Process.  (reap command).
 * Remove and free the process with matching pid from the Termainated Queue.
 * Follow the specification for this function.
 * Returns the exit_code on success or a -1 on any error (such as process not found).
 */
int wlf_reap(Wlf_schedule_s *schedule, pid_t pid) {

 if (schedule == NULL || schedule->terminated_queue->head == NULL) {
    return -1;  // No schedule or terminated queue is empty
  }

  Wlf_process_s *curr = schedule->terminated_queue->head;
  Wlf_process_s *prev = NULL;
  int exit_code = 0;
  int pos = 0;

  // Loop through the terminated queue to find the process
  while (curr != NULL) {
    if (curr->pid == pid || pid == 0) {
      // If we found the process or pid is 0, remove the process
      if (prev == NULL) {
        schedule->terminated_queue->head = curr->next;
      } else {
        prev->next = curr->next;  // Update the previous node's next pointer
      }

      exit_code = wlf_get_ec(curr);  // Get the exit code from the process

      // Free the process memory
      free(curr->cmd);  // Free the command
      free(curr);  // Free the process structure itself

      schedule->terminated_queue->count--;  // Decrease the count of processes
      return exit_code;  // Return the exit code of the reaped process
    }

    prev = curr;
    curr = curr->next;
    pos++;
  }

  return -1;  // If no process was found with the given pid

}

/* Gets the exit code from a terminated process.
 * (All Linux exit codes are between 0 and 255)
 * Follow the project documentation for this function.
 * Returns the exit code if the process is terminated.
 * If the process is not terminated, return -1.
 */
int wlf_get_ec(Wlf_process_s *process) {
  if(process == NULL)
    return -1;

  //gets the exit code
  int temp = process->state;
  temp &= 0x00FF;
  return temp;

}

/* Frees all allocated memory in the Wlf_schedule_s, all of the Queues, and all of their Nodes.
 * Follow the project documentation for this function.
 * Returns void.
 */
void wlf_cleanup(Wlf_schedule_s *schedule) {
  if(schedule==NULL)
    return;
  
  //cleans up the heap
  wlf_cleanup_high(schedule);
  wlf_cleanup_normal(schedule);
  wlf_cleanup_terminate(schedule);

  free(schedule);
}

static void wlf_cleanup_high(Wlf_schedule_s *schedule){
  Wlf_process_s *head = schedule->ready_queue_high->head;
  Wlf_process_s *temp;
  while(head != NULL){
    temp = head;
    head = head->next;

    free(temp->cmd);
    free(temp);
  }
  // free(schedule->ready_queue_high->head);
  free(schedule->ready_queue_high);
}

static void wlf_cleanup_normal(Wlf_schedule_s *schedule){
   Wlf_process_s *head = schedule->ready_queue_normal->head;
  Wlf_process_s *temp;
  while(head != NULL){
    temp = head;
    head = head->next;

    free(temp->cmd);
    free(temp);
  }
  // free(schedule->ready_queue_normal->head);
  free(schedule->ready_queue_normal);
}

static void wlf_cleanup_terminate(Wlf_schedule_s *schedule){
  Wlf_process_s *head = schedule->terminated_queue->head;
  Wlf_process_s *temp;
  while(head != NULL){
    temp = head;
    head = head->next;

    free(temp->cmd);
    free(temp);
  }
  // free(schedule->terminated_queue->head);
  free(schedule->terminated_queue);

}
