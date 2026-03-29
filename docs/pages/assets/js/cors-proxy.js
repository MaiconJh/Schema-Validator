// CORS Proxy for Kilo AI API
// This proxy allows the AI assistant to work from any origin

const https = require('https');
const http = require('http');
const url = require('url');

const PORT = 3001;
const KILO_API_URL = 'https://api.kilo.ai/api/gateway/chat/completions';

const server = http.createServer((req, res) => {
  // Set CORS headers
  res.setHeader('Access-Control-Allow-Origin', '*');
  res.setHeader('Access-Control-Allow-Methods', 'GET, POST, OPTIONS');
  res.setHeader('Access-Control-Allow-Headers', 'Content-Type, Accept, Authorization');
  
  // Handle preflight requests
  if (req.method === 'OPTIONS') {
    res.writeHead(200);
    res.end();
    return;
  }
  
  // Only allow POST requests to /chat/completions
  if (req.method === 'POST' && req.url === '/chat/completions') {
    let body = '';
    
    req.on('data', chunk => {
      body += chunk.toString();
    });
    
    req.on('end', () => {
      try {
        const options = {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
          }
        };
        
        const kiloUrl = new URL(KILO_API_URL);
        const proxyReq = https.request(kiloUrl, options, (proxyRes) => {
          let data = '';
          
          proxyRes.on('data', chunk => {
            data += chunk.toString();
          });
          
          proxyRes.on('end', () => {
            res.writeHead(proxyRes.statusCode, {
              'Content-Type': 'application/json',
              'Access-Control-Allow-Origin': '*'
            });
            res.end(data);
          });
        });
        
        proxyReq.on('error', (error) => {
          console.error('Proxy error:', error);
          res.writeHead(500, { 'Content-Type': 'application/json' });
          res.end(JSON.stringify({ error: 'Proxy error', message: error.message }));
        });
        
        proxyReq.write(body);
        proxyReq.end();
        
      } catch (error) {
        console.error('Request error:', error);
        res.writeHead(400, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify({ error: 'Invalid request', message: error.message }));
      }
    });
    
  } else {
    res.writeHead(404, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({ error: 'Not found' }));
  }
});

server.listen(PORT, () => {
  console.log(`CORS Proxy running on http://localhost:${PORT}`);
  console.log(`Proxying requests to: ${KILO_API_URL}`);
  console.log('Usage: POST http://localhost:3001/chat/completions');
});
