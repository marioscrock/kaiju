package main

import "net"
import "fmt"
import "bufio"
import "os"
import "io"

func main() {

  // connect to this socket
  conn, err := net.Dial("tcp", "kaiju-logs.default:9876")

  path := os.Args[len(os.Args) - 1]

  file, err := os.Open(path)
  defer file.Close()

  if err != nil {
      return
  }

  // Start reading from the file with a reader.
  reader := bufio.NewReader(file)

  var line string
  for {

      line, err = reader.ReadString('\n')
      // send to socket
      fmt.Fprintf(conn, line)

      if err != nil {
          break
      }
  }

  if err != io.EOF {
      fmt.Printf(" > Failed!: %v\n", err)
  }

  return
  
}
