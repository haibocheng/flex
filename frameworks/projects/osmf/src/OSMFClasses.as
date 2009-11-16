////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2005-2009 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package
{

/**
 *  @private
 *  This class is used to link additional classes into osmf.swc.
 */
internal class OSMFClasses
{
	import org.osmf.display.MediaPlayerSprite; MediaPlayerSprite;
	import org.osmf.display.ScaleMode; ScaleMode;
	import org.osmf.events.BufferTimeChangeEvent; BufferTimeChangeEvent;
	import org.osmf.events.BytesDownloadedChangeEvent; BytesDownloadedChangeEvent;
	import org.osmf.events.DimensionChangeEvent; DimensionChangeEvent;
	import org.osmf.events.DurationChangeEvent; DurationChangeEvent;
	import org.osmf.events.LoadableStateChangeEvent; LoadableStateChangeEvent;
	import org.osmf.events.MediaPlayerStateChangeEvent; MediaPlayerStateChangeEvent;
	import org.osmf.events.MutedChangeEvent; MutedChangeEvent;
	import org.osmf.events.PlayheadChangeEvent; PlayheadChangeEvent;
	import org.osmf.events.PlayingChangeEvent; PlayingChangeEvent;
	import org.osmf.events.SeekingChangeEvent; SeekingChangeEvent;
	import org.osmf.events.TraitEvent; TraitEvent;
	import org.osmf.events.VolumeChangeEvent; VolumeChangeEvent;
	import org.osmf.media.IMediaResource; IMediaResource;
	import org.osmf.media.MediaFactory; MediaFactory;
	import org.osmf.media.MediaPlayer; MediaPlayer;
	import org.osmf.media.MediaPlayerState; MediaPlayerState;
	import org.osmf.media.URLResource; URLResource;
	import org.osmf.net.NetLoader; NetLoader;
	import org.osmf.net.NetStreamCodes; NetStreamCodes;
	import org.osmf.net.dynamicstreaming.DynamicStreamingItem; DynamicStreamingItem;
	import org.osmf.net.dynamicstreaming.DynamicStreamingNetLoader; DynamicStreamingNetLoader;
	import org.osmf.net.dynamicstreaming.DynamicStreamingResource; DynamicStreamingResource;
	import org.osmf.traits.ILoadable; ILoadable;
	import org.osmf.traits.LoadState; LoadState;
	import org.osmf.traits.MediaTraitType; MediaTraitType;
	import org.osmf.utils.FMSURL; FMSURL;
	import org.osmf.utils.MediaFrameworkStrings; MediaFrameworkStrings;
	import org.osmf.utils.URL; URL;
	import org.osmf.video.VideoElement; VideoElement;
	// Maintain alphabetical order
}

}

