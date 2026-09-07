local key = KEYS[1]
local capacity = tonumber(ARGV[1])
local refill_rate = tonumber(ARGV[2])
local now = tonumber(ARGV[3])
local requested = tonumber(ARGV[4])

local data = redis.call('HMGET', key, 'tokens', 'last_update')
local tokens = tonumber(data[1])
local last_update = tonumber(data[2])

if not tokens or not last_update then
    tokens = capacity
    last_update = now
else
    local delta = math.max(0, now - last_update)
    tokens = math.min(capacity, tokens + (delta * refill_rate))
    last_update = now
end

if tokens >= requested then
    tokens = tokens - requested
    redis.call('HMSET', key, 'tokens', tokens, 'last_update', last_update)
    redis.call('EXPIRE', key, 3600)
    return 1
else
    redis.call('HMSET', key, 'tokens', tokens, 'last_update', last_update)
    redis.call('EXPIRE', key, 3600)
    return 0
end