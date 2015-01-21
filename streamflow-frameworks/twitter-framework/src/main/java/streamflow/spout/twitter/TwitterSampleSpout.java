/**
 * Copyright 2014 Lockheed Martin Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package streamflow.spout.twitter;

import backtype.storm.Config;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import streamflow.annotations.Component;
import streamflow.annotations.ComponentOutputs;
import streamflow.annotations.ComponentProperty;
import streamflow.annotations.Description;
import streamflow.annotations.ComponentInterface;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

@Component(label = "Twitter Sample Spout", name = "twitter-sample-spout", type = "storm-spout", icon="icons/twitter.png")
@Description("Spouts and Bolts supporting Twitter functionality")
@ComponentOutputs({@ComponentInterface(key = "default", description = "Twitter Status")})
public class TwitterSampleSpout extends BaseRichSpout {
    
    private SpoutOutputCollector collector;

    private Logger logger;

    private String consumerKey;
    private String consumerSecret;
    private String accessToken;
    private String accessTokenSecret;
    private String proxyHost;
    private int proxyPort;

    private final LinkedBlockingQueue<Status> queue = new LinkedBlockingQueue<Status>(100000);
    private TwitterStream twitterStream;


    @ComponentProperty(label = "OAuth Consumer Key", name = "oauth-consumer-key", required = true, type = "text", defaultValue = "")
    @Description("Twitter OAuth Consumer Key")
    @Inject
    public void setConsumerKey(@Named("oauth-consumer-key") String consumerKey) {
        this.consumerKey = consumerKey;
    }

    @ComponentProperty(label = "OAuth Consumer Secret", name = "oauth-consumer-secret", required = true, type = "text", defaultValue = "")
    @Description("Twitter OAuth Consumer Secret")
    @Inject
    public void setConsumerSecret(@Named("oauth-consumer-secret") String consumerSecret) {
        this.consumerSecret = consumerSecret;
    }

    @ComponentProperty(label = "OAuth Access Token", name = "oauth-access-token", required = true, type = "text", defaultValue = "")
    @Description("Twitter OAuth Access Token")
    @Inject
    public void setAccessToken(@Named("oauth-access-token") String accessToken) {
        this.accessToken = accessToken;
    }

    @ComponentProperty(label = "OAuth Access Token Secret", name = "oauth-access-token-secret", required = true, type = "text", defaultValue = "")
    @Description("Twitter OAuth Access Token Secret")
    @Inject
    public void setAccessTokenSecret(@Named("oauth-access-token-secret") String accessTokenSecret) {
        this.accessTokenSecret = accessTokenSecret;
    }

    @Inject
    public void setLogger(Logger logger){
        this.logger = logger;
    }

   // @Inject(optional = true)
    public void setProxyHost(@Named("http.proxy.host") String proxyHost) {
        this.proxyHost = proxyHost;
    }

   // @Inject(optional = true)
    public void setProxyPort(@Named("http.proxy.port") int proxyPort) {
        this.proxyPort = proxyPort;
    }

    @Override
    public void open(Map config, TopologyContext context, SpoutOutputCollector collector) {
        this.collector = collector;

        logger.info("Twitter Sampler Started: Consumer Key = " + consumerKey
            + ", Consumer Secret = " + consumerSecret + ", Access Token = " + accessToken
            + ", Access Token Secret = " + accessTokenSecret);

        if (StringUtils.isNotBlank(consumerKey) && StringUtils.isNotBlank(consumerSecret) &&
                StringUtils.isNotBlank(accessToken) && StringUtils.isNotBlank(accessTokenSecret)) {
            // Build the twitter config to authenticate the requests
            ConfigurationBuilder twitterConfig = new ConfigurationBuilder()
                .setOAuthConsumerKey(consumerKey)
                .setOAuthConsumerSecret(consumerSecret)
                .setOAuthAccessToken(accessToken)
                .setOAuthAccessTokenSecret(accessTokenSecret)
                .setJSONStoreEnabled(true)
                .setIncludeEntitiesEnabled(true)
                .setIncludeEntitiesEnabled(true);

            // Add the proxy settings to the Twitter config if they were specified
            if (StringUtils.isNotBlank(proxyHost) && proxyPort > 0) {
                try {
                    twitterConfig.setHttpProxyPort(proxyPort).setHttpProxyHost(proxyHost);
                }
                catch (Exception ex) {
                }
            }

            // Status listener which handle the status events and add them to the queue
            StatusListener listener = new StatusListener() {
                @Override
                public void onStatus(Status status) {
                    queue.offer(status);
                }

                @Override
                public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
                    logger.debug("Twitter Deletion Notice: " + statusDeletionNotice.getUserId());
                }

                @Override
                public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
                    logger.debug("Twitter On Track Limitation Notice: Number Of Limited Statuses"
                            + numberOfLimitedStatuses);
                }

                @Override
                public void onScrubGeo(long userId, long upToStatusId) {
                    logger.debug("Twitter Scrub Geo: UserID = " + userId
                            + ", UpToStatusId = " + upToStatusId);
                }

                @Override
                public void onException(Exception exception) {
                    logger.debug("Twitter Exception: " + exception.getMessage());
                }

                @Override
                public void onStallWarning(StallWarning stallWarning) {
                    logger.debug("Twitter Stall Warning: " + stallWarning.toString());
                }
            };

            TwitterStreamFactory twitterFactory = new TwitterStreamFactory(twitterConfig.build());
            twitterStream = twitterFactory.getInstance();
            twitterStream.addListener(listener);
            twitterStream.sample();

            logger.info("Twitter Sample Stream Initialized");
            
        } else {
            logger.info("Twitter Sampler missing required OAuth properties. "
                    + "Pleast check your settings and try again.");
        }
    }

    @Override
    public void nextTuple() {
        Status status = queue.poll();

        if (status == null) {
            Utils.sleep(50);
        } else {
            // Emit the twitter status as a JSON String
            collector.emit(new Values(status));
        }
    }

    @Override
    public void close() {
        if (twitterStream != null) {
            twitterStream.shutdown();
        }
        
        logger.info("Twitter Sampler Stopped");
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        Config config = new Config();
        config.setMaxTaskParallelism(1);
        return config;
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("tweet"));
    }
}
