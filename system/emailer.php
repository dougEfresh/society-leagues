<?php

ini_set("display_errors","1");

require_once $_SERVER['DOCUMENT_ROOT'].'/include/swift/lib/swift_required.php';

function _init()
{
	$transport = Swift_SendmailTransport::newInstance('/usr/sbin/sendmail -bs');
	$mailer = Swift_Mailer::newInstance($transport);
	return $mailer;
}

function send_text_email($to, $from, $subject, $text)
{
	$mailer = _init();
	
	$message = Swift_Message::newInstance($subject);
	$message->setFrom(array($from));
	$message->setTo(array($to));
	$message->setBody($text);
	
	$result = $mailer->send($message);
}

function send_html_email($to, $from, $subject, $text, $html, $attachment)
{
	$mailer = _init();
	
	$message = Swift_Message::newInstance($subject);
	$message->setFrom(array($from));
	$message->setTo(array($to));

	foreach($attachment as $a)
	{
		if (file_exists($a))
		{
			$cid = $message->embed(Swift_Image::fromPath($a));
			$html = str_replace($a, $cid, $html);
		}
		else
			error_log("emailer.php: file not found: {$a}");
	}
	
	$message->setBody($html, 'text/html');
	$message->addPart($text, 'text/plain');
	
	$result = $mailer->send($message);
}
?>