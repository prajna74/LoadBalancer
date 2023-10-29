# Build your own application load balancer
This was one of the coding challenges taken from https://codingchallenges.substack.com/p/coding-challenge-5

Current implementation uses round robin algorithm for distributing requests.
Also, it periodically checks the availabilty of backends and stops forwarding requests to the unavailable servers.
As soon as dead server is back and up & running it starts forwarding requests to it.
