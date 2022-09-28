package com.octal.actorPay.mapper;

import java.util.List;

public interface BaseMapper<Source, Destination> {

    public Destination sourceToDestination(Source source);

    public Source destinationToSource(Destination destination);

//	public void sourceToDestination(Source source,@MappingTarget Destination destination);

    public List<Destination> sourceToDestination(List<Source> source);

    public List<Source> destinationToSource(List<Destination> destination);

}
